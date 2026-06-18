package org.example.dao;

import org.example.models.ServicePayment;
import org.example.models.Service;
import org.example.models.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePaymentDAO extends BaseRelationDAO<ServicePayment> {

    public ServicePaymentDAO(Connection connection) {
        super(connection, "public.service_payment", "service_id", "payment_id");
    }

    @Override
    protected int getLeftId(ServicePayment entity) {
        return entity.getServiceId();
    }

    @Override
    protected int getRightId(ServicePayment entity) {
        return entity.getPaymentId();
    }

    @Override
    protected ServicePayment createInstance(int id, int leftId, int rightId) {
        return new ServicePayment(id, leftId, rightId);
    }

    @Override
    protected void setId(ServicePayment entity, int id) {
        entity.setId(id);
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
}