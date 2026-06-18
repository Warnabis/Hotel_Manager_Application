package org.example.dao;

import org.example.models.Guest;
import java.sql.*;

public class GuestDAO extends BaseEntityDAO<Guest> {

    public GuestDAO(Connection connection) {
        super(connection, "public.guest");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.guest (full_name, phone_number, email, status) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Guest guest) throws SQLException {
        pstmt.setString(1, guest.getFullName());
        pstmt.setString(2, guest.getPhoneNumber());
        pstmt.setString(3, guest.getEmail());
        pstmt.setString(4, guest.getStatus());
    }

    @Override
    protected Guest mapRow(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setId(rs.getInt("id"));
        guest.setFullName(rs.getString("full_name"));
        guest.setPhoneNumber(rs.getString("phone_number"));
        guest.setEmail(rs.getString("email"));
        guest.setStatus(rs.getString("status"));
        return guest;
    }

    @Override
    protected void setId(Guest entity, int id) {
        entity.setId(id);
    }

    @Override
    protected int getId(Guest entity) {
        return entity.getId();
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.guest SET full_name = ?, phone_number = ?, email = ?, status = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Guest guest) throws SQLException {
        pstmt.setString(1, guest.getFullName());
        pstmt.setString(2, guest.getPhoneNumber());
        pstmt.setString(3, guest.getEmail());
        pstmt.setString(4, guest.getStatus());
        pstmt.setInt(5, guest.getId());
    }
}