package org.example.dao;

import org.example.models.EmployeePosition;
import org.example.models.Employee;
import org.example.models.Position;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeePositionDAO implements BaseDAO<EmployeePosition> {
    private final Connection connection;

    public EmployeePositionDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(EmployeePosition relation) throws SQLException {
        String sql = "INSERT INTO public.employee_position (employee_id, position_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, relation.getEmployeeId());
            pstmt.setInt(2, relation.getPositionId());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    relation.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(EmployeePosition relation) throws SQLException {
        String sql = "UPDATE public.employee_position SET employee_id = ?, position_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, relation.getEmployeeId());
            pstmt.setInt(2, relation.getPositionId());
            pstmt.setInt(3, relation.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<EmployeePosition> findAll() throws SQLException {
        List<EmployeePosition> relations = new ArrayList<>();
        String sql = "SELECT id, employee_id, position_id FROM public.employee_position ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                relations.add(new EmployeePosition(
                  rs.getInt("id"),
                  rs.getInt("employee_id"),
                  rs.getInt("position_id")
                ));
            }
        }
        return relations;
    }

    @Override
    public EmployeePosition findById(int id) throws SQLException {
        String sql = "SELECT id, employee_id, position_id FROM public.employee_position WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new EmployeePosition(
                      rs.getInt("id"),
                      rs.getInt("employee_id"),
                      rs.getInt("position_id")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.employee_position WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Position> getPositionsByEmployeeId(int employeeId) throws SQLException {
        List<Position> positions = new ArrayList<>();
        String sql = """
            SELECT p.id, p.title, p.salary, p.responsibilities FROM public.position p
            JOIN public.employee_position ep ON p.id = ep.position_id
            WHERE ep.employee_id = ?
            ORDER BY p.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Position position = new Position();
                    position.setId(rs.getInt("id"));
                    position.setTitle(rs.getString("title"));
                    position.setSalary(rs.getBigDecimal("salary"));
                    position.setResponsibilities(rs.getString("responsibilities"));
                    positions.add(position);
                }
            }
        }
        return positions;
    }

    public List<Employee> getEmployeesByPositionId(int positionId) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = """
            SELECT e.id, e.full_name, e.phone_number, e.experience, e.schedule FROM public.employee e
            JOIN public.employee_position ep ON e.id = ep.employee_id
            WHERE ep.position_id = ?
            ORDER BY e.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, positionId);
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

    public boolean exists(int employeeId, int positionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM public.employee_position WHERE employee_id = ? AND position_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            pstmt.setInt(2, positionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void deleteByEmployeeId(int employeeId) throws SQLException {
        String sql = "DELETE FROM public.employee_position WHERE employee_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        }
    }

    public void deleteByPositionId(int positionId) throws SQLException {
        String sql = "DELETE FROM public.employee_position WHERE position_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, positionId);
            pstmt.executeUpdate();
        }
    }
}