package org.example.menu;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.ServiceEmployeeDAO;
import org.example.models.ServiceEmployee;
import org.example.models.Employee;
import org.example.models.Service;
import org.example.utilities.InputHelper;
import java.sql.SQLException;
import java.util.List;

@NoArgsConstructor
@Slf4j
public class ServiceEmployeeMenu {

    private static final String MSG_RELATION_EXISTS = "Такая связь уже существует!";
    private static final String MSG_RELATION_CREATED = "Связь создана! ID: ";
    private static final String MSG_NO_RELATIONS = "Связей не найдено";
    private static final String MSG_TOTAL = "Всего: ";
    private static final String MSG_NOT_FOUND = "Связь не найдена!";
    private static final String MSG_UPDATED = "Связь обновлена!";
    private static final String MSG_NO_CHANGES = "Изменений не было.";
    private static final String MSG_DELETED = "Связь удалена!";
    private static final String MSG_DELETE_CANCELLED = "Удаление отменено.";
    private static final String MSG_SERVICES_DELETED = "Все связи услуги удалены!";
    private static final String MSG_EMPLOYEES_DELETED = "Все связи сотрудника удалены!";

    public static void create(ServiceEmployeeDAO dao) throws SQLException {
        log.info("\n--- Добавление связи услуга-сотрудник ---");
        int serviceId = InputHelper.readInt("ID услуги: ");
        int employeeId = InputHelper.readInt("ID сотрудника: ");
        if (!dao.exists(serviceId, employeeId)) {
            ServiceEmployee relation = new ServiceEmployee(serviceId, employeeId);
            dao.create(relation);
            log.info(MSG_RELATION_CREATED + relation.getId());
        } else {
            log.warn(MSG_RELATION_EXISTS);
        }
    }

    public static void findAll(ServiceEmployeeDAO dao) throws SQLException {
        log.info("\n--- Все связи услуга-сотрудник ---");
        List<ServiceEmployee> relations = dao.findAll();
        if (relations.isEmpty()) {
            log.info(MSG_NO_RELATIONS);
        } else {
            relations.forEach(relation -> log.info(relation.toString()));
            log.info(MSG_TOTAL + relations.size());
        }
    }

    public static void findById(ServiceEmployeeDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        ServiceEmployee relation = dao.findById(id);
        if (relation != null) {
            log.info(relation.toString());
        } else {
            log.warn(MSG_NOT_FOUND);
        }
    }

    public static void update(ServiceEmployeeDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        ServiceEmployee relation = dao.findById(id);
        if (relation == null) {
            log.warn(MSG_NOT_FOUND);
            return;
        }
        log.info("Текущие данные: {}", relation);
        int newServiceId = InputHelper.readOptionalInt("Новый ID услуги [" + relation.getServiceId() + "]: ", relation.getServiceId());
        int newEmployeeId = InputHelper.readOptionalInt("Новый ID сотрудника [" + relation.getEmployeeId() + "]: ", relation.getEmployeeId());
        if (newServiceId != relation.getServiceId() || newEmployeeId != relation.getEmployeeId()) {
            relation.setServiceId(newServiceId);
            relation.setEmployeeId(newEmployeeId);
            dao.update(relation);
            log.info(MSG_UPDATED);
        } else {
            log.info(MSG_NO_CHANGES);
        }
    }

    public static void delete(ServiceEmployeeDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        ServiceEmployee relation = dao.findById(id);
        if (relation == null) {
            log.warn(MSG_NOT_FOUND);
            return;
        }
        if (InputHelper.confirmAction("Удалить связь " + relation)) {
            dao.delete(id);
            log.info(MSG_DELETED);
        } else {
            log.info(MSG_DELETE_CANCELLED);
        }
    }

    public static void showEmployeesByService(ServiceEmployeeDAO dao) throws SQLException {
        int serviceId = InputHelper.readInt("Введите ID услуги: ");
        log.info("\n--- Сотрудники услуги ---");
        List<Employee> employees = dao.getEmployeesByServiceId(serviceId);
        if (employees.isEmpty()) {
            log.info("Сотрудники не найдены");
        } else {
            employees.forEach(employee -> log.info(employee.toString()));
            log.info(MSG_TOTAL + employees.size());
        }
    }

    public static void showServicesByEmployee(ServiceEmployeeDAO dao) throws SQLException {
        int employeeId = InputHelper.readInt("Введите ID сотрудника: ");
        log.info("\n--- Услуги сотрудника ---");
        List<Service> services = dao.getServicesByEmployeeId(employeeId);
        if (services.isEmpty()) {
            log.info("Услуги не найдены");
        } else {
            services.forEach(service -> log.info(service.toString()));
            log.info(MSG_TOTAL + services.size());
        }
    }

    public static void deleteByServiceId(ServiceEmployeeDAO dao) throws SQLException {
        int serviceId = InputHelper.readInt("Введите ID услуги: ");
        if (InputHelper.confirmAction("Удалить все связи услуги")) {
            dao.deleteByServiceId(serviceId);
            log.info(MSG_SERVICES_DELETED);
        } else {
            log.info(MSG_DELETE_CANCELLED);
        }
    }

    public static void deleteByEmployeeId(ServiceEmployeeDAO dao) throws SQLException {
        int employeeId = InputHelper.readInt("Введите ID сотрудника: ");
        if (InputHelper.confirmAction("Удалить все связи сотрудника")) {
            dao.deleteByEmployeeId(employeeId);
            log.info(MSG_EMPLOYEES_DELETED);
        } else {
            log.info(MSG_DELETE_CANCELLED);
        }
    }
}