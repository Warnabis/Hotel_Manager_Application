package org.example.dao;

import org.example.models.ServiceGuest;
import org.example.models.Service;
import org.example.models.Guest;
import java.sql.Connection;
import java.sql.SQLException;
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
        return getRelatedEntities(serviceId, "public.guest", "guest_id",
          "g.id, g.full_name, g.phone_number, g.email, g.status",
          rs -> {
              Guest g = new Guest();
              g.setId(rs.getInt("id"));
              g.setFullName(rs.getString("full_name"));
              g.setPhoneNumber(rs.getString("phone_number"));
              g.setEmail(rs.getString("email"));
              g.setStatus(rs.getString("status"));
              return g;
          });
    }

    public List<Service> getServicesByGuestId(int guestId) throws SQLException {
        return getRelatedEntities(guestId, "public.service", "service_id",
          "s.id, s.title, s.description, s.price, s.duration",
          rs -> {
              Service s = new Service();
              s.setId(rs.getInt("id"));
              s.setTitle(rs.getString("title"));
              s.setDescription(rs.getString("description"));
              s.setPrice(rs.getBigDecimal("price"));
              s.setDuration(rs.getString("duration"));
              return s;
          });
    }
}