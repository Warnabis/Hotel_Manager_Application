package org.example.menu;

import org.example.dao.ServiceEmployeeDAO;
import org.example.models.ServiceEmployee;
import org.example.models.Employee;
import org.example.models.Service;
import org.example.utilities.InputHelper;
import java.sql.SQLException;
import java.util.List;

public class ServiceEmployeeMenu {

    public static void create(ServiceEmployeeDAO dao) throws SQLException {
        System.out.println("\n--- Добавление связи услуга-сотрудник ---");
        int serviceId = InputHelper.readInt("ID услуги: ");
        int employeeId = InputHelper.readInt("ID сотрудника: ");
        if (!dao.exists(serviceId, employeeId)) {
            ServiceEmployee relation = new ServiceEmployee(serviceId, employeeId);
            dao.create(relation);
            System.out.println("Связь создана! ID: " + relation.getId());
        } else {
            System.out.println("Такая связь уже существует!");
        }
    }

    public static void findAll(ServiceEmployeeDAO dao) throws SQLException {
        System.out.println("\n--- Все связи услуга-сотрудник ---");
        List<ServiceEmployee> relations = dao.findAll();
        if (relations.isEmpty()) {
            System.out.println("Связей не найдено");
        } else {
            relations.forEach(System.out::println);
            System.out.println("Всего: " + relations.size());
        }
    }

    public static void findById(ServiceEmployeeDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        ServiceEmployee relation = dao.findById(id);
        if (relation != null) {
            System.out.println(relation);
        } else {
            System.out.println("Связь не найдена!");
        }
    }

    public static void update(ServiceEmployeeDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        ServiceEmployee relation = dao.findById(id);
        if (relation == null) {
            System.out.println("Связь не найдена!");
            return;
        }
        System.out.println("Текущие данные: " + relation);

        int newServiceId = InputHelper.readOptionalInt("Новый ID услуги [" + relation.getServiceId() + "]: ", relation.getServiceId());
        int newEmployeeId = InputHelper.readOptionalInt("Новый ID сотрудника [" + relation.getEmployeeId() + "]: ", relation.getEmployeeId());

        if (newServiceId != relation.getServiceId() || newEmployeeId != relation.getEmployeeId()) {
            relation.setServiceId(newServiceId);
            relation.setEmployeeId(newEmployeeId);
            dao.update(relation);
            System.out.println("Связь обновлена!");
        } else {
            System.out.println("Изменений не было.");
        }
    }

    public static void delete(ServiceEmployeeDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        ServiceEmployee relation = dao.findById(id);
        if (relation == null) {
            System.out.println("Связь не найдена!");
            return;
        }
        if (InputHelper.confirmAction("Удалить связь " + relation)) {
            dao.delete(id);
            System.out.println("Связь удалена!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    public static void showEmployeesByService(ServiceEmployeeDAO dao) throws SQLException {
        int serviceId = InputHelper.readInt("Введите ID услуги: ");
        System.out.println("\n--- Сотрудники услуги ---");
        List<Employee> employees = dao.getEmployeesByServiceId(serviceId);
        if (employees.isEmpty()) {
            System.out.println("Сотрудники не найдены");
        } else {
            employees.forEach(System.out::println);
            System.out.println("Всего: " + employees.size());
        }
    }

    public static void showServicesByEmployee(ServiceEmployeeDAO dao) throws SQLException {
        int employeeId = InputHelper.readInt("Введите ID сотрудника: ");
        System.out.println("\n--- Услуги сотрудника ---");
        List<Service> services = dao.getServicesByEmployeeId(employeeId);
        if (services.isEmpty()) {
            System.out.println("Услуги не найдены");
        } else {
            services.forEach(System.out::println);
            System.out.println("Всего: " + services.size());
        }
    }

    public static void deleteByServiceId(ServiceEmployeeDAO dao) throws SQLException {
        int serviceId = InputHelper.readInt("Введите ID услуги: ");
        if (InputHelper.confirmAction("Удалить все связи услуги")) {
            dao.deleteByServiceId(serviceId);
            System.out.println("Все связи услуги удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    public static void deleteByEmployeeId(ServiceEmployeeDAO dao) throws SQLException {
        int employeeId = InputHelper.readInt("Введите ID сотрудника: ");
        if (InputHelper.confirmAction("Удалить все связи сотрудника")) {
            dao.deleteByEmployeeId(employeeId);
            System.out.println("Все связи сотрудника удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }
}