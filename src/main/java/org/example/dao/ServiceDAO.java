package org.example.dao;

import org.example.models.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO implements BaseDAO<Service> {

    private final Connection connection;

    public ServiceDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Service service) throws SQLException {
        String sql = "INSERT INTO public.service (title, description, price, duration) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, service.getTitle());
            pstmt.setString(2, service.getDescription());
            pstmt.setBigDecimal(3, service.getPrice());
            pstmt.setString(4, service.getDuration());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    service.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Service> findAll() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM public.service ORDER BY id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
        return services;
    }

    @Override
    public Service findById(int id) throws SQLException {
        String sql = "SELECT * FROM public.service WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setTitle(rs.getString("title"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getBigDecimal("price"));
                    service.setDuration(rs.getString("duration"));
                    return service;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Service service) throws SQLException {
        String sql = "UPDATE public.service SET title = ?, description = ?, price = ?, duration = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, service.getTitle());
            pstmt.setString(2, service.getDescription());
            pstmt.setBigDecimal(3, service.getPrice());
            pstmt.setString(4, service.getDuration());
            pstmt.setInt(5, service.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String deleteServiceEmployee = "DELETE FROM public.service_employee WHERE service_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteServiceEmployee)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }

        String deleteServiceGuest = "DELETE FROM public.service_guest WHERE service_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteServiceGuest)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }

        String deleteServicePayment = "DELETE FROM public.service_payment WHERE service_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteServicePayment)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }

        String sql = "DELETE FROM public.service WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}