package org.example.dao;

import org.example.models.ServicePayment;
import org.example.models.Service;
import org.example.models.Payment;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ServicePaymentDAO extends BaseRelationDAO<ServicePayment> {

    public ServicePaymentDAO(Connection connection) {
        super(connection, "public.service_payment", "service_id", "payment_id");
    }

    @Override
    protected int getLeftId(ServicePayment entity) {
        return entity.getServiceId();
    }

    @Override
    protected int getRightId(ServicePayment entity) {
        return entity.getPaymentId();
    }

    @Override
    protected ServicePayment createInstance(int id, int leftId, int rightId) {
        return new ServicePayment(id, leftId, rightId);
    }

    @Override
    protected void setId(ServicePayment entity, int id) {
        entity.setId(id);
    }

    public List<Payment> getPaymentsByServiceId(int serviceId) throws SQLException {
        return getRelatedEntities(serviceId, "public.payment", "payment_id",
          "p.id, p.status, p.amount, p.date, p.method, p.guest_id",
          rs -> {
              Payment p = new Payment();
              p.setId(rs.getInt("id"));
              p.setStatus(rs.getString("status"));
              p.setAmount(rs.getBigDecimal("amount"));
              p.setPaymentDate(rs.getDate("date").toLocalDate());
              p.setPaymentMethod(rs.getString("method"));
              p.setGuestId(rs.getInt("guest_id"));
              return p;
          });
    }

    public List<Service> getServicesByPaymentId(int paymentId) throws SQLException {
        return getRelatedEntities(paymentId, "public.service", "service_id",
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