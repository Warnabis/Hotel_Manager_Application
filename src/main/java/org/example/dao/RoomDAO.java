package org.example.dao;

import org.example.models.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO implements BaseDAO<Room> {

    private final Connection connection;

    public RoomDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Room room) throws SQLException {
        String sql = "INSERT INTO public.room (floor, status, type, price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, room.getFloor());
            pstmt.setString(2, room.getStatus());
            pstmt.setString(3, room.getType());
            pstmt.setBigDecimal(4, room.getPrice());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    room.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Room> findAll() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM public.room ORDER BY id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
        return rooms;
    }

    @Override
    public Room findById(int id) throws SQLException {
        String sql = "SELECT * FROM public.room WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setFloor(rs.getInt("floor"));
                    room.setStatus(rs.getString("status"));
                    room.setType(rs.getString("type"));
                    room.setPrice(rs.getBigDecimal("price"));
                    return room;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Room room) throws SQLException {
        String sql = "UPDATE public.room SET floor = ?, status = ?, type = ?, price = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, room.getFloor());
            pstmt.setString(2, room.getStatus());
            pstmt.setString(3, room.getType());
            pstmt.setBigDecimal(4, room.getPrice());
            pstmt.setInt(5, room.getId());
            pstmt.executeUpdate();
        }
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

        String sql = "DELETE FROM public.room WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}