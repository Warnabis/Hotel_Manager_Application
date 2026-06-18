package org.example.dao;

import org.example.models.BookingPayment;
import org.example.models.Booking;
import org.example.models.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingPaymentDAO implements BaseDAO<BookingPayment> {
    private final Connection connection;

    public BookingPaymentDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(BookingPayment relation) throws SQLException {
        String sql = "INSERT INTO public.booking_payment (booking_id, payment_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, relation.getBookingId());
            pstmt.setInt(2, relation.getPaymentId());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    relation.setId(rs.getInt(1));
                } else {
                    // Если не удалось получить ID через GENERATED_KEYS, используем MAX
                    String maxSql = "SELECT COALESCE(MAX(id), 0) + 1 FROM public.booking_payment";
                    try (Statement stmt = connection.createStatement();
                         ResultSet maxRs = stmt.executeQuery(maxSql)) {
                        if (maxRs.next()) {
                            relation.setId(maxRs.getInt(1));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void update(BookingPayment relation) throws SQLException {
        String sql = "UPDATE public.booking_payment SET booking_id = ?, payment_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, relation.getBookingId());
            pstmt.setInt(2, relation.getPaymentId());
            pstmt.setInt(3, relation.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<BookingPayment> findAll() throws SQLException {
        List<BookingPayment> relations = new ArrayList<>();
        String sql = "SELECT * FROM public.booking_payment ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                relations.add(new BookingPayment(
                  rs.getInt("id"),
                  rs.getInt("booking_id"),
                  rs.getInt("payment_id")
                ));
            }
        }
        return relations;
    }

    @Override
    public BookingPayment findById(int id) throws SQLException {
        String sql = "SELECT * FROM public.booking_payment WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new BookingPayment(
                      rs.getInt("id"),
                      rs.getInt("booking_id"),
                      rs.getInt("payment_id")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.booking_payment WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = """
            SELECT p.* FROM public.payment p
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
            SELECT b.* FROM public.booking b
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

    public boolean exists(int bookingId, int paymentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM public.booking_payment WHERE booking_id = ? AND payment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.setInt(2, paymentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void deleteByBookingId(int bookingId) throws SQLException {
        String sql = "DELETE FROM public.booking_payment WHERE booking_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.executeUpdate();
        }
    }

    public void deleteByPaymentId(int paymentId) throws SQLException {
        String sql = "DELETE FROM public.booking_payment WHERE payment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, paymentId);
            pstmt.executeUpdate();
        }
    }
}