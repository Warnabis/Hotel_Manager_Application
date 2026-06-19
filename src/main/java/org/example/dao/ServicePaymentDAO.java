package org.example.dao;

import org.example.models.ServicePayment;
import org.example.models.Service;
import org.example.models.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePaymentDAO extends BaseRelationDAO<ServicePayment> {

    public ServicePaymentDAO(Connection connection) {
        super(connection, "public.service_payment");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.service_payment (service_id, payment_id) VALUES (?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.service_payment SET service_id = ?, payment_id = ? WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT id, service_id, payment_id FROM public.service_payment ORDER BY id";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id, service_id, payment_id FROM public.service_payment WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM public.service_payment WHERE id = ?";
    }

    @Override
    protected String getExistsSql() {
        return "SELECT COUNT(*) FROM public.service_payment WHERE service_id = ? AND payment_id = ?";
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
    protected ServicePayment createInstance(ResultSet rs) throws SQLException {
        return new ServicePayment(rs.getInt("id"), rs.getInt("service_id"), rs.getInt("payment_id"));
    }

    @Override
    protected void setId(ServicePayment entity, int id) {
        entity.setId(id);
    }

    public List<Payment> getPaymentsByServiceId(int serviceId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = """
            SELECT p.id, p.status, p.amount, p.date, p.method, p.guest_id
            FROM public.payment p
            JOIN public.service_payment sp ON p.id = sp.payment_id
            WHERE sp.service_id = ?
            ORDER BY p.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setId(rs.getInt("id"));
                    p.setStatus(rs.getString("status"));
                    p.setAmount(rs.getBigDecimal("amount"));
                    p.setPaymentDate(rs.getDate("date").toLocalDate());
                    p.setPaymentMethod(rs.getString("method"));
                    p.setGuestId(rs.getInt("guest_id"));
                    payments.add(p);
                }
            }
        }
        return payments;
    }

    public List<Service> getServicesByPaymentId(int paymentId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = """
            SELECT s.id, s.title, s.description, s.price, s.duration
            FROM public.service s
            JOIN public.service_payment sp ON s.id = sp.service_id
            WHERE sp.payment_id = ?
            ORDER BY s.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, paymentId);
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