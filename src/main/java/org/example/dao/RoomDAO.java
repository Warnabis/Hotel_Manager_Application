package org.example.dao;

import org.example.models.Room;
import java.sql.*;

public class RoomDAO extends BaseEntityDAO<Room> {

    public RoomDAO(Connection connection) {
        super(connection, "public.room");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.room (floor, status, type, price) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.room SET floor = ?, status = ?, type = ?, price = ? WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT id, floor, status, type, price FROM public.room ORDER BY id";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id, floor, status, type, price FROM public.room WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM public.room WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Room room) throws SQLException {
        pstmt.setInt(1, room.getFloor());
        pstmt.setString(2, room.getStatus());
        pstmt.setString(3, room.getType());
        pstmt.setBigDecimal(4, room.getPrice());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Room room) throws SQLException {
        pstmt.setInt(1, room.getFloor());
        pstmt.setString(2, room.getStatus());
        pstmt.setString(3, room.getType());
        pstmt.setBigDecimal(4, room.getPrice());
        pstmt.setInt(5, room.getId());
    }

    @Override
    protected Room mapRow(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setFloor(rs.getInt("floor"));
        room.setStatus(rs.getString("status"));
        room.setType(rs.getString("type"));
        room.setPrice(rs.getBigDecimal("price"));
        return room;
    }

    @Override
    protected void setId(Room entity, int id) {
        entity.setId(id);
    }

    @Override
    protected int getId(Room entity) {
        return entity.getId();
    }

    @Override
    public void delete(int id) throws SQLException {
        String deleteRoomBooking = "DELETE FROM public.room_booking WHERE room_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteRoomBooking)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        String deleteRoomEmployee = "DELETE FROM public.room_employee WHERE room_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteRoomEmployee)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        super.delete(id);
    }
}