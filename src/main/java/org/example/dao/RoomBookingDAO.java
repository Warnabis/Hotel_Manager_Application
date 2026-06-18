package org.example.dao;

import org.example.models.RoomBooking;
import org.example.models.Room;
import org.example.models.Booking;

import java.sql.Connection;
import java.sql.SQLException;
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
        return getRelatedEntities(bookingId, "public.room", "room_id",
          "r.id, r.floor, r.status, r.type, r.price",
          rs -> {
              Room r = new Room();
              r.setId(rs.getInt("id"));
              r.setFloor(rs.getInt("floor"));
              r.setStatus(rs.getString("status"));
              r.setType(rs.getString("type"));
              r.setPrice(rs.getBigDecimal("price"));
              return r;
          });
    }

    public List<Booking> getBookingsByRoomId(int roomId) throws SQLException {
        return getRelatedEntities(roomId, "public.booking", "booking_id",
          "b.id, b.price, b.status, b.check_in, b.duration, b.guest_id",
          rs -> {
              Booking b = new Booking();
              b.setId(rs.getInt("id"));
              b.setPrice(rs.getBigDecimal("price"));
              b.setStatus(rs.getString("status"));
              b.setCheckInDate(rs.getDate("check_in").toLocalDate());
              b.setDuration(rs.getString("duration"));
              b.setGuestId(rs.getInt("guest_id"));
              return b;
          });
    }
}