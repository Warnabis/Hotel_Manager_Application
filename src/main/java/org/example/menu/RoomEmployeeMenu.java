package org.example.menu;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.RoomEmployeeDAO;
import org.example.models.RoomEmployee;
import org.example.models.Employee;
import org.example.models.Room;
import org.example.utilities.InputHelper;

import java.sql.SQLException;
import java.util.List;

import static org.example.menu.MenuConstants.*;

@Slf4j
public class RoomEmployeeMenu {

    private RoomEmployeeMenu() {}

    public static void create(RoomEmployeeDAO dao) throws SQLException {
        log.info("\n--- Добавление связи номер-сотрудник ---");
        int roomId = InputHelper.readInt("ID номера: ");
        int employeeId = InputHelper.readInt("ID сотрудника: ");
        if (!dao.exists(roomId, employeeId)) {
            RoomEmployee relation = new RoomEmployee(roomId, employeeId);
            dao.create(relation);
            log.info(MSG_RELATION_CREATED + relation.getId());
        } else {
            log.warn(MSG_RELATION_EXISTS);
        }
    }

    public static void findAll(RoomEmployeeDAO dao) throws SQLException {
        log.info("\n--- Все связи номер-сотрудник ---");
        List<RoomEmployee> relations = dao.findAll();
        if (relations.isEmpty()) {
            log.info(MSG_NO_RELATIONS);
        } else {
            relations.forEach(relation -> log.info(relation.toString()));
            log.info(MSG_TOTAL + relations.size());
        }
    }

    public static void findById(RoomEmployeeDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        RoomEmployee relation = dao.findById(id);
        if (relation != null) {
            log.info(relation.toString());
        } else {
            log.warn(MSG_NOT_FOUND);
        }
    }

    public static void update(RoomEmployeeDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        RoomEmployee relation = dao.findById(id);
        if (relation == null) {
            log.warn(MSG_NOT_FOUND);
            return;
        }
        log.info("Текущие данные: {}", relation);
        int newRoomId = InputHelper.readOptionalInt("Новый ID номера [" + relation.getRoomId() + "]: ", relation.getRoomId());
        int newEmployeeId = InputHelper.readOptionalInt("Новый ID сотрудника [" + relation.getEmployeeId() + "]: ", relation.getEmployeeId());
        if (newRoomId != relation.getRoomId() || newEmployeeId != relation.getEmployeeId()) {
            relation.setRoomId(newRoomId);
            relation.setEmployeeId(newEmployeeId);
            dao.update(relation);
            log.info(MSG_UPDATED);
        } else {
            log.info(MSG_NO_CHANGES);
        }
    }

    public static void delete(RoomEmployeeDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        RoomEmployee relation = dao.findById(id);
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

    public static void showEmployeesByRoom(RoomEmployeeDAO dao) throws SQLException {
        int roomId = InputHelper.readInt("Введите ID номера: ");
        log.info("\n--- Сотрудники номера ---");
        List<Employee> employees = dao.getEmployeesByRoomId(roomId);
        if (employees.isEmpty()) {
            log.info("Сотрудники не найдены");
        } else {
            employees.forEach(employee -> log.info(employee.toString()));
            log.info(MSG_TOTAL + employees.size());
        }
    }

    public static void showRoomsByEmployee(RoomEmployeeDAO dao) throws SQLException {
        int employeeId = InputHelper.readInt("Введите ID сотрудника: ");
        log.info("\n--- Номера сотрудника ---");
        List<Room> rooms = dao.getRoomsByEmployeeId(employeeId);
        if (rooms.isEmpty()) {
            log.info("Номера не найдены");
        } else {
            rooms.forEach(room -> log.info(room.toString()));
            log.info(MSG_TOTAL + rooms.size());
        }
    }
}