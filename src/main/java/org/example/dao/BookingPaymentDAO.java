package org.example.dao;

import org.example.models.BookingPayment;
import org.example.models.Booking;
import org.example.models.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingPaymentDAO extends BaseRelationDAO<BookingPayment> {

    public BookingPaymentDAO(Connection connection) {
        super(connection, "public.booking_payment");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.booking_payment (booking_id, payment_id) VALUES (?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.booking_payment SET booking_id = ?, payment_id = ? WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT id, booking_id, payment_id FROM public.booking_payment ORDER BY id";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id, booking_id, payment_id FROM public.booking_payment WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM public.booking_payment WHERE id = ?";
    }

    @Override
    protected String getExistsSql() {
        return "SELECT COUNT(*) FROM public.booking_payment WHERE booking_id = ? AND payment_id = ?";
    }

    @Override
    protected int getLeftId(BookingPayment entity) {
        return entity.getBookingId();
    }

    @Override
    protected int getRightId(BookingPayment entity) {
        return entity.getPaymentId();
    }

    @Override
    protected BookingPayment createInstance(ResultSet rs) throws SQLException {
        return new BookingPayment(rs.getInt("id"), rs.getInt("booking_id"), rs.getInt("payment_id"));
    }

    @Override
    protected void setId(BookingPayment entity, int id) {
        entity.setId(id);
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = """
            SELECT p.id, p.status, p.amount, p.date, p.method, p.guest_id
            FROM public.payment p
            JOIN public.booking_payment bp ON p.id = bp.payment_id
            WHERE bp.booking_id = ?
            ORDER BY p.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
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

    public List<Booking> getBookingsByPaymentId(int paymentId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.id, b.price, b.status, b.check_in, b.duration, b.guest_id
            FROM public.booking b
            JOIN public.booking_payment bp ON b.id = bp.booking_id
            WHERE bp.payment_id = ?
            ORDER BY b.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, paymentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = new Booking();
                    b.setId(rs.getInt("id"));
                    b.setPrice(rs.getBigDecimal("price"));
                    b.setStatus(rs.getString("status"));
                    b.setCheckInDate(rs.getDate("check_in").toLocalDate());
                    b.setDuration(rs.getString("duration"));
                    b.setGuestId(rs.getInt("guest_id"));
                    bookings.add(b);
                }
            }
        }
        return bookings;
    }
}