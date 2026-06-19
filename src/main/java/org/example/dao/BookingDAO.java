package org.example.dao;

import org.example.models.Booking;
import java.sql.*;

public class BookingDAO extends BaseEntityDAO<Booking> {

    public BookingDAO(Connection connection) {
        super(connection, "public.booking");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.booking (price, duration, check_in, status, guest_id) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.booking SET price = ?, duration = ?, check_in = ?, status = ?, guest_id = ? WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT id, price, status, check_in, duration, guest_id FROM public.booking ORDER BY id";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id, price, status, check_in, duration, guest_id FROM public.booking WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM public.booking WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Booking booking) throws SQLException {
        pstmt.setBigDecimal(1, booking.getPrice());
        pstmt.setString(2, booking.getDuration());
        pstmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
        pstmt.setString(4, booking.getStatus());
        pstmt.setInt(5, booking.getGuestId());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Booking booking) throws SQLException {
        pstmt.setBigDecimal(1, booking.getPrice());
        pstmt.setString(2, booking.getDuration());
        pstmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
        pstmt.setString(4, booking.getStatus());
        pstmt.setInt(5, booking.getGuestId());
        pstmt.setInt(6, booking.getId());
    }

    @Override
    protected Booking mapRow(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setPrice(rs.getBigDecimal("price"));
        booking.setStatus(rs.getString("status"));
        booking.setCheckInDate(rs.getDate("check_in").toLocalDate());
        booking.setDuration(rs.getString("duration"));
        booking.setGuestId(rs.getInt("guest_id"));
        return booking;
    }

    @Override
    protected void setId(Booking entity, int id) {
        entity.setId(id);
    }

    @Override
    protected int getId(Booking entity) {
        return entity.getId();
    }
}