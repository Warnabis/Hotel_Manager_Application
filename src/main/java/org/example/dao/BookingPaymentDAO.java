package org.example.dao;

import org.example.models.BookingPayment;
import org.example.models.Booking;
import org.example.models.Payment;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookingPaymentDAO extends BaseRelationDAO<BookingPayment> {

    public BookingPaymentDAO(Connection connection) {
        super(connection, "public.booking_payment", "booking_id", "payment_id");
    }

    @Override
    protected int getLeftId(BookingPayment entity) {
        return entity.getBookingId();
    }

    @Override
    protected int getRightId(BookingPayment entity) {
        return entity.getPaymentId();
    }

    @Override
    protected BookingPayment createInstance(int id, int leftId, int rightId) {
        return new BookingPayment(id, leftId, rightId);
    }

    @Override
    protected void setId(BookingPayment entity, int id) {
        entity.setId(id);
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) throws SQLException {
        return getRelatedEntities(bookingId, "public.payment", "payment_id",
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

    public List<Booking> getBookingsByPaymentId(int paymentId) throws SQLException {
        return getRelatedEntities(paymentId, "public.booking", "booking_id",
          "b.id, b.price, b.status, b.check_in, b.duration, b.guest_id",
          rs -> {
              Booking b = new Booking();
              b.setId(rs.getInt("id"));
              b.setPrice(rs.getBigDecimal("price"));
              b.setStatus(rs.getString("status"));
              b.setCheckInDate(rs.getDate("check_in").toLocalDate());
              b.setDuration(rs.getString("duration"));
              b.setGuestId(rs.getInt("guest_id"));
              return b;
          });
    }
}