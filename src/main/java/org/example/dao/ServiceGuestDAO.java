package org.example.dao;

import org.example.models.ServiceGuest;
import org.example.models.Service;
import org.example.models.Guest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceGuestDAO implements BaseDAO<ServiceGuest> {
    private final Connection connection;

    public ServiceGuestDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(ServiceGuest relation) throws SQLException {
        String sql = "INSERT INTO public.service_guest (service_id, guest_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, relation.getServiceId());
            pstmt.setInt(2, relation.getGuestId());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    relation.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(ServiceGuest relation) throws SQLException {
        String sql = "UPDATE public.service_guest SET service_id = ?, guest_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, relation.getServiceId());
            pstmt.setInt(2, relation.getGuestId());
            pstmt.setInt(3, relation.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<ServiceGuest> findAll() throws SQLException {
        List<ServiceGuest> relations = new ArrayList<>();
        String sql = "SELECT id, service_id, guest_id FROM public.service_guest ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                relations.add(new ServiceGuest(
                  rs.getInt("id"),
                  rs.getInt("service_id"),
                  rs.getInt("guest_id")
                ));
            }
        }
        return relations;
    }

    @Override
    public ServiceGuest findById(int id) throws SQLException {
        String sql = "SELECT id, service_id, guest_id FROM public.service_guest WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ServiceGuest(
                      rs.getInt("id"),
                      rs.getInt("service_id"),
                      rs.getInt("guest_id")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.service_guest WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
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

    public boolean exists(int serviceId, int guestId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM public.service_guest WHERE service_id = ? AND guest_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            pstmt.setInt(2, guestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void deleteByServiceId(int serviceId) throws SQLException {
        String sql = "DELETE FROM public.service_guest WHERE service_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            pstmt.executeUpdate();
        }
    }

    public void deleteByGuestId(int guestId) throws SQLException {
        String sql = "DELETE FROM public.service_guest WHERE guest_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, guestId);
            pstmt.executeUpdate();
        }
    }
}