package org.example.dao;

import org.example.models.ServiceGuest;
import org.example.models.Service;
import org.example.models.Guest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceGuestDAO extends BaseRelationDAO<ServiceGuest> {

    public ServiceGuestDAO(Connection connection) {
        super(connection, "public.service_guest", "service_id", "guest_id");
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
    protected ServiceGuest createInstance(int id, int leftId, int rightId) {
        return new ServiceGuest(id, leftId, rightId);
    }

    @Override
    protected void setId(ServiceGuest entity, int id) {
        entity.setId(id);
    }

    public List<Guest> getGuestsByServiceId(int serviceId) throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String sql = """
            SELECT g.id, g.full_name, g.phone_number, g.email, g.status FROM public.guest g
            JOIN public.service_guest sg ON g.id = sg.guest_id
            WHERE sg.service_id = ?
            ORDER BY g.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Guest guest = new Guest();
                    guest.setId(rs.getInt("id"));
                    guest.setFullName(rs.getString("full_name"));
                    guest.setPhoneNumber(rs.getString("phone_number"));
                    guest.setEmail(rs.getString("email"));
                    guest.setStatus(rs.getString("status"));
                    guests.add(guest);
                }
            }
        }
        return guests;
    }

    public List<Service> getServicesByGuestId(int guestId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = """
            SELECT s.id, s.title, s.description, s.price, s.duration FROM public.service s
            JOIN public.service_guest sg ON s.id = sg.service_id
            WHERE sg.guest_id = ?
            ORDER BY s.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, guestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setTitle(rs.getString("title"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getBigDecimal("price"));
                    service.setDuration(rs.getString("duration"));
                    services.add(service);
                }
            }
        }
        return services;
    }
}