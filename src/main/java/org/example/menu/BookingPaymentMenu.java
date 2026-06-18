package org.example.menu;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.BookingPaymentDAO;
import org.example.models.BookingPayment;
import org.example.models.Payment;
import org.example.models.Booking;
import org.example.utilities.InputHelper;

import java.sql.SQLException;
import java.util.List;

import static org.example.menu.MenuConstants.*;

@Slf4j
public class BookingPaymentMenu {

    private BookingPaymentMenu() {}

    public static void create(BookingPaymentDAO dao) throws SQLException {
        log.info("\n--- Добавление связи бронирование-платеж ---");
        int bookingId = InputHelper.readInt("ID бронирования: ");
        int paymentId = InputHelper.readInt("ID платежа: ");
        if (!dao.exists(bookingId, paymentId)) {
            BookingPayment relation = new BookingPayment(bookingId, paymentId);
            dao.create(relation);
            log.info(MSG_RELATION_CREATED + relation.getId());
        } else {
            log.warn(MSG_RELATION_EXISTS);
        }
    }

    public static void findAll(BookingPaymentDAO dao) throws SQLException {
        log.info("\n--- Все связи бронирование-платеж ---");
        List<BookingPayment> relations = dao.findAll();
        if (relations.isEmpty()) {
            log.info(MSG_NO_RELATIONS);
        } else {
            relations.forEach(relation -> log.info(relation.toString()));
            log.info(MSG_TOTAL + relations.size());
        }
    }

    public static void findById(BookingPaymentDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        BookingPayment relation = dao.findById(id);
        if (relation != null) {
            log.info(relation.toString());
        } else {
            log.warn(MSG_NOT_FOUND);
        }
    }

    public static void update(BookingPaymentDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        BookingPayment relation = dao.findById(id);
        if (relation == null) {
            log.warn(MSG_NOT_FOUND);
            return;
        }
        log.info("Текущие данные: {}", relation);
        int newBookingId = InputHelper.readOptionalInt("Новый ID бронирования [" + relation.getBookingId() + "]: ", relation.getBookingId());
        int newPaymentId = InputHelper.readOptionalInt("Новый ID платежа [" + relation.getPaymentId() + "]: ", relation.getPaymentId());
        if (newBookingId != relation.getBookingId() || newPaymentId != relation.getPaymentId()) {
            relation.setBookingId(newBookingId);
            relation.setPaymentId(newPaymentId);
            dao.update(relation);
            log.info(MSG_UPDATED);
        } else {
            log.info(MSG_NO_CHANGES);
        }
    }

    public static void delete(BookingPaymentDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        BookingPayment relation = dao.findById(id);
        if (relation == null) {
            log.warn(MSG_NOT_FOUND);
            return;
        }
        if (InputHelper.confirmAction("Удалить связь " + relation)) {
            dao.delete(id);
            log.info(MSG_DELETED);
        } else {
            log.info(MSG_DELETE_CANCELLED);
        }
    }

    public static void showPaymentsByBooking(BookingPaymentDAO dao) throws SQLException {
        int bookingId = InputHelper.readInt("Введите ID бронирования: ");
        log.info("\n--- Платежи бронирования ---");
        List<Payment> payments = dao.getPaymentsByBookingId(bookingId);
        if (payments.isEmpty()) {
            log.info("Платежи не найдены");
        } else {
            payments.forEach(payment -> log.info(payment.toString()));
            log.info(MSG_TOTAL + payments.size());
        }
    }

    public static void showBookingsByPayment(BookingPaymentDAO dao) throws SQLException {
        int paymentId = InputHelper.readInt("Введите ID платежа: ");
        log.info("\n--- Бронирования платежа ---");
        List<Booking> bookings = dao.getBookingsByPaymentId(paymentId);
        if (bookings.isEmpty()) {
            log.info("Бронирования не найдены");
        } else {
            bookings.forEach(booking -> log.info(booking.toString()));
            log.info(MSG_TOTAL + bookings.size());
        }
    }

}