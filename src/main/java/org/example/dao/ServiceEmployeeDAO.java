package org.example.dao;

import org.example.models.ServiceEmployee;
import org.example.models.Service;
import org.example.models.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEmployeeDAO implements BaseDAO<ServiceEmployee> {
    private final Connection connection;

    public ServiceEmployeeDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(ServiceEmployee relation) throws SQLException {
        String sql = "INSERT INTO public.service_employee (service_id, employee_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, relation.getServiceId());
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
    public void update(ServiceEmployee relation) throws SQLException {
        String sql = "UPDATE public.service_employee SET service_id = ?, employee_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, relation.getServiceId());
            pstmt.setInt(2, relation.getEmployeeId());
            pstmt.setInt(3, relation.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<ServiceEmployee> findAll() throws SQLException {
        List<ServiceEmployee> relations = new ArrayList<>();
        String sql = "SELECT id, service_id, employee_id FROM public.service_employee ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                relations.add(new ServiceEmployee(
                  rs.getInt("id"),
                  rs.getInt("service_id"),
                  rs.getInt("employee_id")
                ));
            }
        }
        return relations;
    }

    @Override
    public ServiceEmployee findById(int id) throws SQLException {
        String sql = "SELECT id, service_id, employee_id FROM public.service_employee WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ServiceEmployee(
                      rs.getInt("id"),
                      rs.getInt("service_id"),
                      rs.getInt("employee_id")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.service_employee WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Employee> getEmployeesByServiceId(int serviceId) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = """
            SELECT e.id, e.full_name, e.phone_number, e.experience, e.schedule FROM public.employee e
            JOIN public.service_employee se ON e.id = se.employee_id
            WHERE se.service_id = ?
            ORDER BY e.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
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

    public List<Service> getServicesByEmployeeId(int employeeId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = """
            SELECT s.id, s.title, s.description, s.price, s.duration FROM public.service s
            JOIN public.service_employee se ON s.id = se.service_id
            WHERE se.employee_id = ?
            ORDER BY s.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setTitle(rs.getString("title"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getBigDecimal("price"));
                    service.setDuration(rs.getString("duration"));
                    services.add(service);
                }
            }
        }
        return services;
    }

    public boolean exists(int serviceId, int employeeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM public.service_employee WHERE service_id = ? AND employee_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            pstmt.setInt(2, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void deleteByServiceId(int serviceId) throws SQLException {
        String sql = "DELETE FROM public.service_employee WHERE service_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            pstmt.executeUpdate();
        }
    }

    public void deleteByEmployeeId(int employeeId) throws SQLException {
        String sql = "DELETE FROM public.service_employee WHERE employee_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        }
    }
}