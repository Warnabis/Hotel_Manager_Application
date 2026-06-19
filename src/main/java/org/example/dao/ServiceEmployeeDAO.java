package org.example.dao;

import org.example.models.ServiceEmployee;
import org.example.models.Service;
import org.example.models.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEmployeeDAO extends BaseRelationDAO<ServiceEmployee> {

    public ServiceEmployeeDAO(Connection connection) {
        super(connection, "public.service_employee");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.service_employee (service_id, employee_id) VALUES (?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.service_employee SET service_id = ?, employee_id = ? WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT id, service_id, employee_id FROM public.service_employee ORDER BY id";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id, service_id, employee_id FROM public.service_employee WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM public.service_employee WHERE id = ?";
    }

    @Override
    protected String getExistsSql() {
        return "SELECT COUNT(*) FROM public.service_employee WHERE service_id = ? AND employee_id = ?";
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
    protected ServiceEmployee createInstance(ResultSet rs) throws SQLException {
        return new ServiceEmployee(rs.getInt("id"), rs.getInt("service_id"), rs.getInt("employee_id"));
    }

    @Override
    protected void setId(ServiceEmployee entity, int id) {
        entity.setId(id);
    }

    public List<Employee> getEmployeesByServiceId(int serviceId) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = """
            SELECT e.id, e.full_name, e.phone_number, e.experience, e.schedule
            FROM public.employee e
            JOIN public.service_employee se ON e.id = se.employee_id
            WHERE se.service_id = ?
            ORDER BY e.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
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

    public List<Service> getServicesByEmployeeId(int employeeId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = """
            SELECT s.id, s.title, s.description, s.price, s.duration
            FROM public.service s
            JOIN public.service_employee se ON s.id = se.service_id
            WHERE se.employee_id = ?
            ORDER BY s.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Service s = new Service();
                    s.setId(rs.getInt("id"));
                    s.setTitle(rs.getString("title"));
                    s.setDescription(rs.getString("description"));
                    s.setPrice(rs.getBigDecimal("price"));
                    s.setDuration(rs.getString("duration"));
                    services.add(s);
                }
            }
        }
        return services;
    }
}