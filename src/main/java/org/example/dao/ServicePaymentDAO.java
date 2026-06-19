package org.example.dao;

import org.example.models.ServicePayment;
import org.example.models.Service;
import org.example.models.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePaymentDAO implements BaseDAO<ServicePayment> {
    private final Connection connection;

    public ServicePaymentDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(ServicePayment relation) throws SQLException {
        String sql = "INSERT INTO public.service_payment (service_id, payment_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, relation.getServiceId());
            pstmt.setInt(2, relation.getPaymentId());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    relation.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(ServicePayment relation) throws SQLException {
        String sql = "UPDATE public.service_payment SET service_id = ?, payment_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, relation.getServiceId());
            pstmt.setInt(2, relation.getPaymentId());
            pstmt.setInt(3, relation.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<ServicePayment> findAll() throws SQLException {
        List<ServicePayment> relations = new ArrayList<>();
        String sql = "SELECT id, service_id, payment_id FROM public.service_payment ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                relations.add(new ServicePayment(
                  rs.getInt("id"),
                  rs.getInt("service_id"),
                  rs.getInt("payment_id")
                ));
            }
        }
        return relations;
    }

    @Override
    public ServicePayment findById(int id) throws SQLException {
        String sql = "SELECT id, service_id, payment_id FROM public.service_payment WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ServicePayment(
                      rs.getInt("id"),
                      rs.getInt("service_id"),
                      rs.getInt("payment_id")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.service_payment WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Payment> getPaymentsByServiceId(int serviceId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = """
            SELECT p.id, p.status, p.amount, p.date, p.method, p.guest_id FROM public.payment p
            JOIN public.service_payment sp ON p.id = sp.payment_id
            WHERE sp.service_id = ?
            ORDER BY p.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();
                    payment.setId(rs.getInt("id"));
                    payment.setStatus(rs.getString("status"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setPaymentDate(rs.getDate("date").toLocalDate());
                    payment.setPaymentMethod(rs.getString("method"));
                    payment.setGuestId(rs.getInt("guest_id"));
                    payments.add(payment);
                }
            }
        }
        return payments;
    }

    public List<Service> getServicesByPaymentId(int paymentId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = """
            SELECT s.id, s.title, s.description, s.price, s.duration FROM public.service s
            JOIN public.service_payment sp ON s.id = sp.service_id
            WHERE sp.payment_id = ?
            ORDER BY s.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, paymentId);
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

    public boolean exists(int serviceId, int paymentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM public.service_payment WHERE service_id = ? AND payment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            pstmt.setInt(2, paymentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void deleteByServiceId(int serviceId) throws SQLException {
        String sql = "DELETE FROM public.service_payment WHERE service_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            pstmt.executeUpdate();
        }
    }

    public void deleteByPaymentId(int paymentId) throws SQLException {
        String sql = "DELETE FROM public.service_payment WHERE payment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, paymentId);
            pstmt.executeUpdate();
        }
    }
}