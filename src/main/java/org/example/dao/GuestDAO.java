package org.example.dao;

import org.example.models.Guest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO implements BaseDAO<Guest> {

    private final Connection connection;

    public GuestDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Guest guest) {
        if (guest == null) throw new IllegalArgumentException("Гость не может быть null");
        String sql = "INSERT INTO public.guest (full_name, phone_number, email, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, guest.getFullName());
            pstmt.setString(2, guest.getPhoneNumber());
            pstmt.setString(3, guest.getEmail());
            pstmt.setString(4, guest.getStatus());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) guest.setId(rs.getInt(1));
                else {
                    String maxSql = "SELECT COALESCE(MAX(id), 0) + 1 FROM public.guest";
                    try (Statement stmt = connection.createStatement();
                         ResultSet maxRs = stmt.executeQuery(maxSql)) {
                        if (maxRs.next()) guest.setId(maxRs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании гостя: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Guest> findAll() {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT id, full_name, phone_number, email, status FROM public.guest ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Guest guest = new Guest();
                guest.setId(rs.getInt("id"));
                guest.setFullName(rs.getString("full_name"));
                guest.setPhoneNumber(rs.getString("phone_number"));
                guest.setEmail(rs.getString("email"));
                guest.setStatus(rs.getString("status"));
                guests.add(guest);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка гостей: " + e.getMessage(), e);
        }
        return guests;
    }

    @Override
    public Guest findById(int id) {
        String sql = "SELECT id, full_name, phone_number, email, status FROM public.guest WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Guest guest = new Guest();
                    guest.setId(rs.getInt("id"));
                    guest.setFullName(rs.getString("full_name"));
                    guest.setPhoneNumber(rs.getString("phone_number"));
                    guest.setEmail(rs.getString("email"));
                    guest.setStatus(rs.getString("status"));
                    return guest;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске гостя по id " + id + ": " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void update(Guest guest) {
        if (guest == null) throw new IllegalArgumentException("Гость не может быть null");
        String sql = "UPDATE public.guest SET full_name = ?, phone_number = ?, email = ?, status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, guest.getFullName());
            pstmt.setString(2, guest.getPhoneNumber());
            pstmt.setString(3, guest.getEmail());
            pstmt.setString(4, guest.getStatus());
            pstmt.setInt(5, guest.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении гостя: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM public.guest WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении гостя: " + e.getMessage(), e);
        }
    }
}