package org.example.dao;

import org.example.models.RoomBooking;
import org.example.models.Room;
import org.example.models.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomBookingDAO extends BaseRelationDAO<RoomBooking> {

    public RoomBookingDAO(Connection connection) {
        super(connection, "public.room_booking");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.room_booking (room_id, booking_id) VALUES (?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.room_booking SET room_id = ?, booking_id = ? WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT id, room_id, booking_id FROM public.room_booking ORDER BY id";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id, room_id, booking_id FROM public.room_booking WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM public.room_booking WHERE id = ?";
    }

    @Override
    protected String getExistsSql() {
        return "SELECT COUNT(*) FROM public.room_booking WHERE room_id = ? AND booking_id = ?";
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
    protected RoomBooking createInstance(ResultSet rs) throws SQLException {
        return new RoomBooking(rs.getInt("id"), rs.getInt("room_id"), rs.getInt("booking_id"));
    }

    @Override
    protected void setId(RoomBooking entity, int id) {
        entity.setId(id);
    }

    public List<Room> getRoomsByBookingId(int bookingId) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = """
            SELECT r.id, r.floor, r.status, r.type, r.price
            FROM public.room r
            JOIN public.room_booking rb ON r.id = rb.room_id
            WHERE rb.booking_id = ?
            ORDER BY r.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Room r = new Room();
                    r.setId(rs.getInt("id"));
                    r.setFloor(rs.getInt("floor"));
                    r.setStatus(rs.getString("status"));
                    r.setType(rs.getString("type"));
                    r.setPrice(rs.getBigDecimal("price"));
                    rooms.add(r);
                }
            }
        }
        return rooms;
    }

    public List<Booking> getBookingsByRoomId(int roomId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.id, b.price, b.status, b.check_in, b.duration, b.guest_id
            FROM public.booking b
            JOIN public.room_booking rb ON b.id = rb.booking_id
            WHERE rb.room_id = ?
            ORDER BY b.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
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