package org.example.dao;

import org.example.models.BookingPayment;
import org.example.models.Booking;
import org.example.models.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingPaymentDAO extends BaseRelationDAO<BookingPayment> {

    public BookingPaymentDAO(Connection connection) {
        super(connection, "public.booking_payment", "booking_id", "payment_id");
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
    protected BookingPayment createInstance(int id, int leftId, int rightId) {
        return new BookingPayment(id, leftId, rightId);
    }

    @Override
    protected void setId(BookingPayment entity, int id) {
        entity.setId(id);
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = """
            SELECT p.id, p.status, p.amount, p.date, p.method, p.guest_id FROM public.payment p
            JOIN public.booking_payment bp ON p.id = bp.payment_id
            WHERE bp.booking_id = ?
            ORDER BY p.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
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

    public List<Booking> getBookingsByPaymentId(int paymentId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.id, b.price, b.status, b.check_in, b.duration, b.guest_id FROM public.booking b
            JOIN public.booking_payment bp ON b.id = bp.booking_id
            WHERE bp.payment_id = ?
            ORDER BY b.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, paymentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setPrice(rs.getBigDecimal("price"));
                    booking.setStatus(rs.getString("status"));
                    booking.setCheckInDate(rs.getDate("check_in").toLocalDate());
                    booking.setDuration(rs.getString("duration"));
                    booking.setGuestId(rs.getInt("guest_id"));
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }
}