package org.example.dao;

import org.example.models.RoomEmployee;
import org.example.models.Room;
import org.example.models.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomEmployeeDAO extends BaseRelationDAO<RoomEmployee> {

    public RoomEmployeeDAO(Connection connection) {
        super(connection, "public.room_employee", "room_id", "employee_id");
    }

    @Override
    protected int getLeftId(RoomEmployee entity) {
        return entity.getRoomId();
    }

    @Override
    protected int getRightId(RoomEmployee entity) {
        return entity.getEmployeeId();
    }

    @Override
    protected RoomEmployee createInstance(int id, int leftId, int rightId) {
        return new RoomEmployee(id, leftId, rightId);
    }

    @Override
    protected void setId(RoomEmployee entity, int id) {
        entity.setId(id);
    }

    public List<Employee> getEmployeesByRoomId(int roomId) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = """
            SELECT e.id, e.full_name, e.phone_number, e.experience, e.schedule FROM public.employee e
            JOIN public.room_employee re ON e.id = re.employee_id
            WHERE re.room_id = ?
            ORDER BY e.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId(rs.getInt("id"));
                    employee.setFullName(rs.getString("full_name"));
                    employee.setPhoneNumber(rs.getString("phone_number"));
                    employee.setExperience(rs.getString("experience"));
                    employee.setSchedule(rs.getString("schedule"));
                    employees.add(employee);
                }
            }
        }
        return employees;
    }

    public List<Room> getRoomsByEmployeeId(int employeeId) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = """
            SELECT r.id, r.floor, r.status, r.type, r.price FROM public.room r
            JOIN public.room_employee re ON r.id = re.room_id
            WHERE re.employee_id = ?
            ORDER BY r.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setFloor(rs.getInt("floor"));
                    room.setStatus(rs.getString("status"));
                    room.setType(rs.getString("type"));
                    room.setPrice(rs.getBigDecimal("price"));
                    rooms.add(room);
                }
            }
        }
        return rooms;
    }
}