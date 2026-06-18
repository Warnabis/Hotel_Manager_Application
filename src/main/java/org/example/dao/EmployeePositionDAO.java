package org.example.dao;

import org.example.models.EmployeePosition;
import org.example.models.Employee;
import org.example.models.Position;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeePositionDAO extends BaseRelationDAO<EmployeePosition> {

    public EmployeePositionDAO(Connection connection) {
        super(connection, "public.employee_position", "employee_id", "position_id");
    }

    @Override
    protected int getLeftId(EmployeePosition entity) {
        return entity.getEmployeeId();
    }

    @Override
    protected int getRightId(EmployeePosition entity) {
        return entity.getPositionId();
    }

    @Override
    protected EmployeePosition createInstance(int id, int leftId, int rightId) {
        return new EmployeePosition(id, leftId, rightId);
    }

    @Override
    protected void setId(EmployeePosition entity, int id) {
        entity.setId(id);
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
}