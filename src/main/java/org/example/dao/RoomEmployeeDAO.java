package org.example.dao;

import org.example.models.RoomEmployee;
import org.example.models.Room;
import org.example.models.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomEmployeeDAO implements BaseDAO<RoomEmployee> {
    private final Connection connection;

    public RoomEmployeeDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(RoomEmployee relation) throws SQLException {
        String sql = "INSERT INTO public.room_employee (room_id, employee_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, relation.getRoomId());
            pstmt.setInt(2, relation.getEmployeeId());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    relation.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(RoomEmployee relation) throws SQLException {
        String sql = "UPDATE public.room_employee SET room_id = ?, employee_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, relation.getRoomId());
            pstmt.setInt(2, relation.getEmployeeId());
            pstmt.setInt(3, relation.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<RoomEmployee> findAll() throws SQLException {
        List<RoomEmployee> relations = new ArrayList<>();
        String sql = "SELECT id, room_id, employee_id FROM public.room_employee ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                relations.add(new RoomEmployee(
                  rs.getInt("id"),
                  rs.getInt("room_id"),
                  rs.getInt("employee_id")
                ));
            }
        }
        return relations;
    }

    @Override
    public RoomEmployee findById(int id) throws SQLException {
        String sql = "SELECT id, room_id, employee_id FROM public.room_employee WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new RoomEmployee(
                      rs.getInt("id"),
                      rs.getInt("room_id"),
                      rs.getInt("employee_id")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.room_employee WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
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

    public boolean exists(int roomId, int employeeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM public.room_employee WHERE room_id = ? AND employee_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.setInt(2, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void deleteByRoomId(int roomId) throws SQLException {
        String sql = "DELETE FROM public.room_employee WHERE room_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.executeUpdate();
        }
    }

    public void deleteByEmployeeId(int employeeId) throws SQLException {
        String sql = "DELETE FROM public.room_employee WHERE employee_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        }
    }
}