package org.example.menu;

import org.example.dao.RoomEmployeeDAO;
import org.example.models.RoomEmployee;
import org.example.models.Employee;
import org.example.models.Room;
import org.example.utilities.InputHelper;
import java.sql.SQLException;
import java.util.List;

public class RoomEmployeeMenu {

    public static void create(RoomEmployeeDAO dao) throws SQLException {
        System.out.println("\n--- Добавление связи номер-сотрудник ---");
        int roomId = InputHelper.readInt("ID номера: ");
        int employeeId = InputHelper.readInt("ID сотрудника: ");
        if (!dao.exists(roomId, employeeId)) {
            RoomEmployee relation = new RoomEmployee(roomId, employeeId);
            dao.create(relation);
            System.out.println("Связь создана! ID: " + relation.getId());
        } else {
            System.out.println("Такая связь уже существует!");
        }
    }

    public static void findAll(RoomEmployeeDAO dao) throws SQLException {
        System.out.println("\n--- Все связи номер-сотрудник ---");
        List<RoomEmployee> relations = dao.findAll();
        if (relations.isEmpty()) {
            System.out.println("Связей не найдено");
        } else {
            relations.forEach(System.out::println);
            System.out.println("Всего: " + relations.size());
        }
    }

    public static void findById(RoomEmployeeDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        RoomEmployee relation = dao.findById(id);
        if (relation != null) {
            System.out.println(relation);
        } else {
            System.out.println("Связь не найдена!");
        }
    }

    public static void update(RoomEmployeeDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        RoomEmployee relation = dao.findById(id);
        if (relation == null) {
            System.out.println("Связь не найдена!");
            return;
        }
        System.out.println("Текущие данные: " + relation);

        int newRoomId = InputHelper.readOptionalInt("Новый ID номера [" + relation.getRoomId() + "]: ", relation.getRoomId());
        int newEmployeeId = InputHelper.readOptionalInt("Новый ID сотрудника [" + relation.getEmployeeId() + "]: ", relation.getEmployeeId());

        if (newRoomId != relation.getRoomId() || newEmployeeId != relation.getEmployeeId()) {
            relation.setRoomId(newRoomId);
            relation.setEmployeeId(newEmployeeId);
            dao.update(relation);
            System.out.println("Связь обновлена!");
        } else {
            System.out.println("Изменений не было.");
        }
    }

    public static void delete(RoomEmployeeDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        RoomEmployee relation = dao.findById(id);
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

    public static void showEmployeesByRoom(RoomEmployeeDAO dao) throws SQLException {
        int roomId = InputHelper.readInt("Введите ID номера: ");
        System.out.println("\n--- Сотрудники номера ---");
        List<Employee> employees = dao.getEmployeesByRoomId(roomId);
        if (employees.isEmpty()) {
            System.out.println("Сотрудники не найдены");
        } else {
            employees.forEach(System.out::println);
            System.out.println("Всего: " + employees.size());
        }
    }

    public static void showRoomsByEmployee(RoomEmployeeDAO dao) throws SQLException {
        int employeeId = InputHelper.readInt("Введите ID сотрудника: ");
        System.out.println("\n--- Номера сотрудника ---");
        List<Room> rooms = dao.getRoomsByEmployeeId(employeeId);
        if (rooms.isEmpty()) {
            System.out.println("Номера не найдены");
        } else {
            rooms.forEach(System.out::println);
            System.out.println("Всего: " + rooms.size());
        }
    }

    public static void deleteByRoomId(RoomEmployeeDAO dao) throws SQLException {
        int roomId = InputHelper.readInt("Введите ID номера: ");
        if (InputHelper.confirmAction("Удалить все связи номера")) {
            dao.deleteByRoomId(roomId);
            System.out.println("Все связи номера удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    public static void deleteByEmployeeId(RoomEmployeeDAO dao) throws SQLException {
        int employeeId = InputHelper.readInt("Введите ID сотрудника: ");
        if (InputHelper.confirmAction("Удалить все связи сотрудника")) {
            dao.deleteByEmployeeId(employeeId);
            System.out.println("Все связи сотрудника удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }
}