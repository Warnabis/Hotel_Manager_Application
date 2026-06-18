package org.example.dao;

import org.example.models.Employee;
import java.sql.*;

public class EmployeeDAO extends BaseEntityDAO<Employee> {

    public EmployeeDAO(Connection connection) {
        super(connection, "public.employee");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.employee (full_name, phone_number, experience, schedule) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Employee employee) throws SQLException {
        pstmt.setString(1, employee.getFullName());
        pstmt.setString(2, employee.getPhoneNumber());
        pstmt.setString(3, employee.getExperience());
        pstmt.setString(4, employee.getSchedule());
    }

    @Override
    protected Employee mapRow(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getInt("id"));
        employee.setFullName(rs.getString("full_name"));
        employee.setPhoneNumber(rs.getString("phone_number"));
        employee.setExperience(rs.getString("experience"));
        employee.setSchedule(rs.getString("schedule"));
        return employee;
    }

    @Override
    protected void setId(Employee entity, int id) {
        entity.setId(id);
    }

    @Override
    protected int getId(Employee entity) {
        return entity.getId();
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.employee SET full_name = ?, phone_number = ?, experience = ?, schedule = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Employee employee) throws SQLException {
        pstmt.setString(1, employee.getFullName());
        pstmt.setString(2, employee.getPhoneNumber());
        pstmt.setString(3, employee.getExperience());
        pstmt.setString(4, employee.getSchedule());
        pstmt.setInt(5, employee.getId());
    }
}