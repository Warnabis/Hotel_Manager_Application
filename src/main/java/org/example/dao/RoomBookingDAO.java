package org.example.dao;

import org.example.models.RoomBooking;
import org.example.models.Room;
import org.example.models.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomBookingDAO extends BaseRelationDAO<RoomBooking> {

    public RoomBookingDAO(Connection connection) {
        super(connection, "public.room_booking", "room_id", "booking_id");
    }

    @Override
    protected int getLeftId(RoomBooking entity) {
        return entity.getRoomId();
    }

    @Override
    protected int getRightId(RoomBooking entity) {
        return entity.getBookingId();
    }

    @Override
    protected RoomBooking createInstance(int id, int leftId, int rightId) {
        return new RoomBooking(id, leftId, rightId);
    }

    @Override
    protected void setId(RoomBooking entity, int id) {
        entity.setId(id);
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
}