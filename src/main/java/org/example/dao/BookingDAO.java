package org.example.dao;

import org.example.models.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO implements BaseDAO<Booking> {

    private final Connection connection;

    public BookingDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Booking booking) throws SQLException {
        String sql = "INSERT INTO public.booking (price, duration, check_in, status, guest_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setBigDecimal(1, booking.getPrice());
            pstmt.setString(2, booking.getDuration());
            pstmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
            pstmt.setString(4, booking.getStatus());
            pstmt.setInt(5, booking.getGuestId());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    booking.setId(rs.getInt(1));
                } else {
                    String maxSql = "SELECT COALESCE(MAX(id), 0) + 1 FROM public.booking";
                    try (Statement stmt = connection.createStatement();
                         ResultSet maxRs = stmt.executeQuery(maxSql)) {
                        if (maxRs.next()) {
                            booking.setId(maxRs.getInt(1));
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<Booking> findAll() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT id, price, status, check_in, duration, guest_id FROM public.booking ORDER BY id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
        return bookings;
    }

    @Override
    public Booking findById(int id) throws SQLException {
        String sql = "SELECT id, price, status, check_in, duration, guest_id FROM public.booking WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setPrice(rs.getBigDecimal("price"));
                    booking.setStatus(rs.getString("status"));
                    booking.setCheckInDate(rs.getDate("check_in").toLocalDate());
                    booking.setDuration(rs.getString("duration"));
                    booking.setGuestId(rs.getInt("guest_id"));
                    return booking;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Booking booking) throws SQLException {
        String sql = "UPDATE public.booking SET price = ?, duration = ?, check_in = ?, status = ?, guest_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, booking.getPrice());
            pstmt.setString(2, booking.getDuration());
            pstmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
            pstmt.setString(4, booking.getStatus());
            pstmt.setInt(5, booking.getGuestId());
            pstmt.setInt(6, booking.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.booking WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}