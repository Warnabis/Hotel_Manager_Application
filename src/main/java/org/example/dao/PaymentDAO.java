package org.example.dao;

import org.example.models.Payment;
import java.sql.*;

public class PaymentDAO extends BaseEntityDAO<Payment> {

    public PaymentDAO(Connection connection) {
        super(connection, "public.payment");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.payment (status, amount, date, method, guest_id) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Payment payment) throws SQLException {
        pstmt.setString(1, payment.getStatus());
        pstmt.setBigDecimal(2, payment.getAmount());
        pstmt.setDate(3, Date.valueOf(payment.getPaymentDate()));
        pstmt.setString(4, payment.getPaymentMethod());
        pstmt.setInt(5, payment.getGuestId());
    }

    @Override
    protected Payment mapRow(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setStatus(rs.getString("status"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentDate(rs.getDate("date").toLocalDate());
        payment.setPaymentMethod(rs.getString("method"));
        payment.setGuestId(rs.getInt("guest_id"));
        return payment;
    }

    @Override
    protected void setId(Payment entity, int id) {
        entity.setId(id);
    }

    @Override
    protected int getId(Payment entity) {
        return entity.getId();
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.payment SET status = ?, amount = ?, date = ?, method = ?, guest_id = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Payment payment) throws SQLException {
        pstmt.setString(1, payment.getStatus());
        pstmt.setBigDecimal(2, payment.getAmount());
        pstmt.setDate(3, Date.valueOf(payment.getPaymentDate()));
        pstmt.setString(4, payment.getPaymentMethod());
        pstmt.setInt(5, payment.getGuestId());
        pstmt.setInt(6, payment.getId());
    }
}