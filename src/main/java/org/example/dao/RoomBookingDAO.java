package org.example.dao;

import org.example.models.RoomBooking;
import org.example.models.Room;
import org.example.models.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomBookingDAO implements BaseDAO<RoomBooking> {
    private final Connection connection;

    public RoomBookingDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(RoomBooking relation) throws SQLException {
        String sql = "INSERT INTO public.room_booking (room_id, booking_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, relation.getRoomId());
            pstmt.setInt(2, relation.getBookingId());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    relation.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(RoomBooking relation) throws SQLException {
        String sql = "UPDATE public.room_booking SET room_id = ?, booking_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, relation.getRoomId());
            pstmt.setInt(2, relation.getBookingId());
            pstmt.setInt(3, relation.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<RoomBooking> findAll() throws SQLException {
        List<RoomBooking> relations = new ArrayList<>();
        String sql = "SELECT id, room_id, booking_id FROM public.room_booking ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                relations.add(new RoomBooking(
                  rs.getInt("id"),
                  rs.getInt("room_id"),
                  rs.getInt("booking_id")
                ));
            }
        }
        return relations;
    }

    @Override
    public RoomBooking findById(int id) throws SQLException {
        String sql = "SELECT id, room_id, booking_id FROM public.room_booking WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new RoomBooking(
                      rs.getInt("id"),
                      rs.getInt("room_id"),
                      rs.getInt("booking_id")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.room_booking WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Room> getRoomsByBookingId(int bookingId) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = """
            SELECT r.id, r.floor, r.status, r.type, r.price FROM public.room r
            JOIN public.room_booking rb ON r.id = rb.room_id
            WHERE rb.booking_id = ?
            ORDER BY r.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setFloor(rs.getInt("floor"));
                    room.setStatus(rs.getString("status"));
                    room.setType(rs.getString("type"));
                    room.setPrice(rs.getBigDecimal("price"));
                    rooms.add(room);
                }
            }
        }
        return rooms;
    }

    public List<Booking> getBookingsByRoomId(int roomId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.id, b.price, b.status, b.check_in, b.duration, b.guest_id FROM public.booking b
            JOIN public.room_booking rb ON b.id = rb.booking_id
            WHERE rb.room_id = ?
            ORDER BY b.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
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

    public boolean exists(int roomId, int bookingId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM public.room_booking WHERE room_id = ? AND booking_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.setInt(2, bookingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void deleteByRoomId(int roomId) throws SQLException {
        String sql = "DELETE FROM public.room_booking WHERE room_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.executeUpdate();
        }
    }

    public void deleteByBookingId(int bookingId) throws SQLException {
        String sql = "DELETE FROM public.room_booking WHERE booking_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.executeUpdate();
        }
    }
}