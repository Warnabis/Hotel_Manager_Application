package org.example.dao;

import org.example.models.ServiceGuest;
import org.example.models.Service;
import org.example.models.Guest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceGuestDAO extends BaseRelationDAO<ServiceGuest> {

    public ServiceGuestDAO(Connection connection) {
        super(connection, "public.service_guest");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.service_guest (service_id, guest_id) VALUES (?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.service_guest SET service_id = ?, guest_id = ? WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT id, service_id, guest_id FROM public.service_guest ORDER BY id";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id, service_id, guest_id FROM public.service_guest WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM public.service_guest WHERE id = ?";
    }

    @Override
    protected String getExistsSql() {
        return "SELECT COUNT(*) FROM public.service_guest WHERE service_id = ? AND guest_id = ?";
    }

    @Override
    protected int getLeftId(ServiceGuest entity) {
        return entity.getServiceId();
    }

    @Override
    protected int getRightId(ServiceGuest entity) {
        return entity.getGuestId();
    }

    @Override
    protected ServiceGuest createInstance(ResultSet rs) throws SQLException {
        return new ServiceGuest(rs.getInt("id"), rs.getInt("service_id"), rs.getInt("guest_id"));
    }

    @Override
    protected void setId(ServiceGuest entity, int id) {
        entity.setId(id);
    }

    public List<Guest> getGuestsByServiceId(int serviceId) throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String sql = """
            SELECT g.id, g.full_name, g.phone_number, g.email, g.status
            FROM public.guest g
            JOIN public.service_guest sg ON g.id = sg.guest_id
            WHERE sg.service_id = ?
            ORDER BY g.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Guest g = new Guest();
                    g.setId(rs.getInt("id"));
                    g.setFullName(rs.getString("full_name"));
                    g.setPhoneNumber(rs.getString("phone_number"));
                    g.setEmail(rs.getString("email"));
                    g.setStatus(rs.getString("status"));
                    guests.add(g);
                }
            }
        }
        return guests;
    }

    public List<Service> getServicesByGuestId(int guestId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = """
            SELECT s.id, s.title, s.description, s.price, s.duration
            FROM public.service s
            JOIN public.service_guest sg ON s.id = sg.service_id
            WHERE sg.guest_id = ?
            ORDER BY s.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, guestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Service s = new Service();
                    s.setId(rs.getInt("id"));
                    s.setTitle(rs.getString("title"));
                    s.setDescription(rs.getString("description"));
                    s.setPrice(rs.getBigDecimal("price"));
                    s.setDuration(rs.getString("duration"));
                    services.add(s);
                }
            }
        }
        return services;
    }
}