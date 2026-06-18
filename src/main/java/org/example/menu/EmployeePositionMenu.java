package org.example.menu;

import org.example.dao.EmployeePositionDAO;
import org.example.models.EmployeePosition;
import org.example.models.Position;
import org.example.models.Employee;
import org.example.utilities.InputHelper;
import java.sql.SQLException;
import java.util.List;

public class EmployeePositionMenu {

    public static void create(EmployeePositionDAO dao) throws SQLException {
        System.out.println("\n--- Добавление связи сотрудник-должность ---");
        int employeeId = InputHelper.readInt("ID сотрудника: ");
        int positionId = InputHelper.readInt("ID должности: ");
        if (!dao.exists(employeeId, positionId)) {
            EmployeePosition relation = new EmployeePosition(employeeId, positionId);
            dao.create(relation);
            System.out.println("Связь создана! ID: " + relation.getId());
        } else {
            System.out.println("Такая связь уже существует!");
        }
    }

    public static void findAll(EmployeePositionDAO dao) throws SQLException {
        System.out.println("\n--- Все связи сотрудник-должность ---");
        List<EmployeePosition> relations = dao.findAll();
        if (relations.isEmpty()) System.out.println("Связей не найдено");
        else { relations.forEach(System.out::println); System.out.println("Всего: " + relations.size()); }
    }

    public static void findById(EmployeePositionDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        EmployeePosition relation = dao.findById(id);
        if (relation != null) System.out.println(relation);
        else System.out.println("Связь не найдена!");
    }

    public static void update(EmployeePositionDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        EmployeePosition relation = dao.findById(id);
        if (relation == null) { System.out.println("Связь не найдена!"); return; }
        System.out.println("Текущие данные: " + relation);
        int newEmployeeId = InputHelper.readOptionalInt("Новый ID сотрудника [" + relation.getEmployeeId() + "]: ", relation.getEmployeeId());
        int newPositionId = InputHelper.readOptionalInt("Новый ID должности [" + relation.getPositionId() + "]: ", relation.getPositionId());
        if (newEmployeeId != relation.getEmployeeId() || newPositionId != relation.getPositionId()) {
            relation.setEmployeeId(newEmployeeId);
            relation.setPositionId(newPositionId);
            dao.update(relation);
            System.out.println("Связь обновлена!");
        } else {
            System.out.println("Изменений не было.");
        }
    }

    public static void delete(EmployeePositionDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        EmployeePosition relation = dao.findById(id);
        if (relation == null) { System.out.println("Связь не найдена!"); return; }
        if (InputHelper.confirmAction("Удалить связь " + relation)) {
            dao.delete(id);
            System.out.println("Связь удалена!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    public static void showPositionsByEmployee(EmployeePositionDAO dao) throws SQLException {
        int employeeId = InputHelper.readInt("Введите ID сотрудника: ");
        System.out.println("\n--- Должности сотрудника ---");
        List<Position> positions = dao.getPositionsByEmployeeId(employeeId);
        if (positions.isEmpty()) System.out.println("Должности не найдены");
        else { positions.forEach(System.out::println); System.out.println("Всего: " + positions.size()); }
    }

    public static void showEmployeesByPosition(EmployeePositionDAO dao) throws SQLException {
        int positionId = InputHelper.readInt("Введите ID должности: ");
        System.out.println("\n--- Сотрудники должности ---");
        List<Employee> employees = dao.getEmployeesByPositionId(positionId);
        if (employees.isEmpty()) System.out.println("Сотрудники не найдены");
        else { employees.forEach(System.out::println); System.out.println("Всего: " + employees.size()); }
    }

    public static void deleteByEmployeeId(EmployeePositionDAO dao) throws SQLException {
        int employeeId = InputHelper.readInt("Введите ID сотрудника: ");
        if (InputHelper.confirmAction("Удалить все связи сотрудника")) {
            dao.deleteByEmployeeId(employeeId);
            System.out.println("Все связи сотрудника удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    public static void deleteByPositionId(EmployeePositionDAO dao) throws SQLException {
        int positionId = InputHelper.readInt("Введите ID должности: ");
        if (InputHelper.confirmAction("Удалить все связи должности")) {
            dao.deleteByPositionId(positionId);
            System.out.println("Все связи должности удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }
}