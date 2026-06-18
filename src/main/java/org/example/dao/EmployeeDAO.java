package org.example.dao;

import org.example.models.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO implements BaseDAO<Employee> {

    private final Connection connection;

    public EmployeeDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Employee employee) throws SQLException {
        String sql = "INSERT INTO public.employee (full_name, phone_number, experience, schedule) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, employee.getFullName());
            pstmt.setString(2, employee.getPhoneNumber());
            pstmt.setString(3, employee.getExperience());
            pstmt.setString(4, employee.getSchedule());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    employee.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Employee> findAll() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM public.employee ORDER BY id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
        return employees;
    }

    @Override
    public Employee findById(int id) throws SQLException {
        String sql = "SELECT * FROM public.employee WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId(rs.getInt("id"));
                    employee.setFullName(rs.getString("full_name"));
                    employee.setPhoneNumber(rs.getString("phone_number"));
                    employee.setExperience(rs.getString("experience"));
                    employee.setSchedule(rs.getString("schedule"));
                    return employee;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Employee employee) throws SQLException {
        String sql = "UPDATE public.employee SET full_name = ?, phone_number = ?, experience = ?, schedule = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employee.getFullName());
            pstmt.setString(2, employee.getPhoneNumber());
            pstmt.setString(3, employee.getExperience());
            pstmt.setString(4, employee.getSchedule());
            pstmt.setInt(5, employee.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.employee WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }


}