package org.example.dao;

import org.example.models.EmployeePosition;
import org.example.models.Employee;
import org.example.models.Position;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeePositionDAO extends BaseRelationDAO<EmployeePosition> {

    public EmployeePositionDAO(Connection connection) {
        super(connection, "public.employee_position");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.employee_position (employee_id, position_id) VALUES (?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.employee_position SET employee_id = ?, position_id = ? WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT id, employee_id, position_id FROM public.employee_position ORDER BY id";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id, employee_id, position_id FROM public.employee_position WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM public.employee_position WHERE id = ?";
    }

    @Override
    protected String getExistsSql() {
        return "SELECT COUNT(*) FROM public.employee_position WHERE employee_id = ? AND position_id = ?";
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
    protected EmployeePosition createInstance(ResultSet rs) throws SQLException {
        return new EmployeePosition(rs.getInt("id"), rs.getInt("employee_id"), rs.getInt("position_id"));
    }

    @Override
    protected void setId(EmployeePosition entity, int id) {
        entity.setId(id);
    }

    public List<Position> getPositionsByEmployeeId(int employeeId) throws SQLException {
        List<Position> positions = new ArrayList<>();
        String sql = """
            SELECT p.id, p.title, p.salary, p.responsibilities
            FROM public.position p
            JOIN public.employee_position ep ON p.id = ep.position_id
            WHERE ep.employee_id = ?
            ORDER BY p.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Position p = new Position();
                    p.setId(rs.getInt("id"));
                    p.setTitle(rs.getString("title"));
                    p.setSalary(rs.getBigDecimal("salary"));
                    p.setResponsibilities(rs.getString("responsibilities"));
                    positions.add(p);
                }
            }
        }
        return positions;
    }

    public List<Employee> getEmployeesByPositionId(int positionId) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = """
            SELECT e.id, e.full_name, e.phone_number, e.experience, e.schedule
            FROM public.employee e
            JOIN public.employee_position ep ON e.id = ep.employee_id
            WHERE ep.position_id = ?
            ORDER BY e.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, positionId);
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
}