package org.example.dao;

import org.example.models.Payment;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO implements BaseDAO<Payment> {

    private final Connection connection;

    public PaymentDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Payment payment) throws SQLException {
        String sql = "INSERT INTO public.payment (status, amount, date, method, guest_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, payment.getStatus());
            pstmt.setBigDecimal(2, payment.getAmount());
            pstmt.setDate(3, Date.valueOf(payment.getPaymentDate()));
            pstmt.setString(4, payment.getPaymentMethod());
            pstmt.setInt(5, payment.getGuestId());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    payment.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Payment> findAll() throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT id, status, amount, date, method, guest_id FROM public.payment ORDER BY id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
        return payments;
    }

    @Override
    public Payment findById(int id) throws SQLException {
        String sql = "SELECT id, status, amount, date, method, guest_id FROM public.payment WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.setId(rs.getInt("id"));
                    payment.setStatus(rs.getString("status"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setPaymentDate(rs.getDate("date").toLocalDate());
                    payment.setPaymentMethod(rs.getString("method"));
                    payment.setGuestId(rs.getInt("guest_id"));
                    return payment;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Payment payment) throws SQLException {
        String sql = "UPDATE public.payment SET status = ?, amount = ?, date = ?, method = ?, guest_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, payment.getStatus());
            pstmt.setBigDecimal(2, payment.getAmount());
            pstmt.setDate(3, Date.valueOf(payment.getPaymentDate()));
            pstmt.setString(4, payment.getPaymentMethod());
            pstmt.setInt(5, payment.getGuestId());
            pstmt.setInt(6, payment.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.payment WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}