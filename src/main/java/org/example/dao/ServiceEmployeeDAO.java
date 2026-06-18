package org.example.dao;

import org.example.models.ServiceEmployee;
import org.example.models.Service;
import org.example.models.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEmployeeDAO extends BaseRelationDAO<ServiceEmployee> {

    public ServiceEmployeeDAO(Connection connection) {
        super(connection, "public.service_employee", "service_id", "employee_id");
    }

    @Override
    protected int getLeftId(ServiceEmployee entity) {
        return entity.getServiceId();
    }

    @Override
    protected int getRightId(ServiceEmployee entity) {
        return entity.getEmployeeId();
    }

    @Override
    protected ServiceEmployee createInstance(int id, int leftId, int rightId) {
        return new ServiceEmployee(id, leftId, rightId);
    }

    @Override
    protected void setId(ServiceEmployee entity, int id) {
        entity.setId(id);
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
}