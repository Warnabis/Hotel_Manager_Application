package org.example.dao;

import org.example.models.RoomEmployee;
import org.example.models.Room;
import org.example.models.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomEmployeeDAO extends BaseRelationDAO<RoomEmployee> {

    public RoomEmployeeDAO(Connection connection) {
        super(connection, "public.room_employee");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.room_employee (room_id, employee_id) VALUES (?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.room_employee SET room_id = ?, employee_id = ? WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT id, room_id, employee_id FROM public.room_employee ORDER BY id";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id, room_id, employee_id FROM public.room_employee WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM public.room_employee WHERE id = ?";
    }

    @Override
    protected String getExistsSql() {
        return "SELECT COUNT(*) FROM public.room_employee WHERE room_id = ? AND employee_id = ?";
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
    protected RoomEmployee createInstance(ResultSet rs) throws SQLException {
        return new RoomEmployee(rs.getInt("id"), rs.getInt("room_id"), rs.getInt("employee_id"));
    }

    @Override
    protected void setId(RoomEmployee entity, int id) {
        entity.setId(id);
    }

    public List<Employee> getEmployeesByRoomId(int roomId) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = """
            SELECT e.id, e.full_name, e.phone_number, e.experience, e.schedule
            FROM public.employee e
            JOIN public.room_employee re ON e.id = re.employee_id
            WHERE re.room_id = ?
            ORDER BY e.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Employee e = new Employee();
                    e.setId(rs.getInt("id"));
                    e.setFullName(rs.getString("full_name"));
                    e.setPhoneNumber(rs.getString("phone_number"));
                    e.setExperience(rs.getString("experience"));
                    e.setSchedule(rs.getString("schedule"));
                    employees.add(e);
                }
            }
        }
        return employees;
    }

    public List<Room> getRoomsByEmployeeId(int employeeId) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = """
            SELECT r.id, r.floor, r.status, r.type, r.price
            FROM public.room r
            JOIN public.room_employee re ON r.id = re.room_id
            WHERE re.employee_id = ?
            ORDER BY r.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Room r = new Room();
                    r.setId(rs.getInt("id"));
                    r.setFloor(rs.getInt("floor"));
                    r.setStatus(rs.getString("status"));
                    r.setType(rs.getString("type"));
                    r.setPrice(rs.getBigDecimal("price"));
                    rooms.add(r);
                }
            }
        }
        return rooms;
    }
}