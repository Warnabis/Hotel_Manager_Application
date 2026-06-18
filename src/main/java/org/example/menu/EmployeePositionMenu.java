package org.example.menu;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.EmployeePositionDAO;
import org.example.models.EmployeePosition;
import org.example.models.Position;
import org.example.models.Employee;
import org.example.utilities.InputHelper;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class EmployeePositionMenu {

    private static final String MSG_RELATION_EXISTS = "Такая связь уже существует!";
    private static final String MSG_RELATION_CREATED = "Связь создана! ID: ";
    private static final String MSG_NO_RELATIONS = "Связей не найдено";
    private static final String MSG_TOTAL = "Всего: ";
    private static final String MSG_NOT_FOUND = "Связь не найдена!";
    private static final String MSG_UPDATED = "Связь обновлена!";
    private static final String MSG_NO_CHANGES = "Изменений не было.";
    private static final String MSG_DELETED = "Связь удалена!";
    private static final String MSG_DELETE_CANCELLED = "Удаление отменено.";
    private static final String MSG_EMPLOYEES_DELETED = "Все связи сотрудника удалены!";
    private static final String MSG_POSITIONS_DELETED = "Все связи должности удалены!";

    public static void create(EmployeePositionDAO dao) throws SQLException {
        log.info("\n--- Добавление связи сотрудник-должность ---");
        int employeeId = InputHelper.readInt("ID сотрудника: ");
        int positionId = InputHelper.readInt("ID должности: ");
        if (!dao.exists(employeeId, positionId)) {
            EmployeePosition relation = new EmployeePosition(employeeId, positionId);
            dao.create(relation);
            log.info(MSG_RELATION_CREATED + relation.getId());
        } else {
            log.warn(MSG_RELATION_EXISTS);
        }
    }

    public static void findAll(EmployeePositionDAO dao) throws SQLException {
        log.info("\n--- Все связи сотрудник-должность ---");
        List<EmployeePosition> relations = dao.findAll();
        if (relations.isEmpty()) {
            log.info(MSG_NO_RELATIONS);
        } else {
            relations.forEach(relation -> log.info(relation.toString()));
            log.info(MSG_TOTAL + relations.size());
        }
    }

    public static void findById(EmployeePositionDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        EmployeePosition relation = dao.findById(id);
        if (relation != null) {
            log.info(relation.toString());
        } else {
            log.warn(MSG_NOT_FOUND);
        }
    }

    public static void update(EmployeePositionDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        EmployeePosition relation = dao.findById(id);
        if (relation == null) {
            log.warn(MSG_NOT_FOUND);
            return;
        }
        log.info("Текущие данные: {}", relation);
        int newEmployeeId = InputHelper.readOptionalInt("Новый ID сотрудника [" + relation.getEmployeeId() + "]: ", relation.getEmployeeId());
        int newPositionId = InputHelper.readOptionalInt("Новый ID должности [" + relation.getPositionId() + "]: ", relation.getPositionId());
        if (newEmployeeId != relation.getEmployeeId() || newPositionId != relation.getPositionId()) {
            relation.setEmployeeId(newEmployeeId);
            relation.setPositionId(newPositionId);
            dao.update(relation);
            log.info(MSG_UPDATED);
        } else {
            log.info(MSG_NO_CHANGES);
        }
    }

    public static void delete(EmployeePositionDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        EmployeePosition relation = dao.findById(id);
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

    public static void showPositionsByEmployee(EmployeePositionDAO dao) throws SQLException {
        int employeeId = InputHelper.readInt("Введите ID сотрудника: ");
        log.info("\n--- Должности сотрудника ---");
        List<Position> positions = dao.getPositionsByEmployeeId(employeeId);
        if (positions.isEmpty()) {
            log.info("Должности не найдены");
        } else {
            positions.forEach(position -> log.info(position.toString()));
            log.info(MSG_TOTAL + positions.size());
        }
    }

    public static void showEmployeesByPosition(EmployeePositionDAO dao) throws SQLException {
        int positionId = InputHelper.readInt("Введите ID должности: ");
        log.info("\n--- Сотрудники должности ---");
        List<Employee> employees = dao.getEmployeesByPositionId(positionId);
        if (employees.isEmpty()) {
            log.info("Сотрудники не найдены");
        } else {
            employees.forEach(employee -> log.info(employee.toString()));
            log.info(MSG_TOTAL + employees.size());
        }
    }

    public static void deleteByEmployeeId(EmployeePositionDAO dao) throws SQLException {
        int employeeId = InputHelper.readInt("Введите ID сотрудника: ");
        if (InputHelper.confirmAction("Удалить все связи сотрудника")) {
            dao.deleteByEmployeeId(employeeId);
            log.info(MSG_EMPLOYEES_DELETED);
        } else {
            log.info(MSG_DELETE_CANCELLED);
        }
    }

    public static void deleteByPositionId(EmployeePositionDAO dao) throws SQLException {
        int positionId = InputHelper.readInt("Введите ID должности: ");
        if (InputHelper.confirmAction("Удалить все связи должности")) {
            dao.deleteByPositionId(positionId);
            log.info(MSG_POSITIONS_DELETED);
        } else {
            log.info(MSG_DELETE_CANCELLED);
        }
    }
}