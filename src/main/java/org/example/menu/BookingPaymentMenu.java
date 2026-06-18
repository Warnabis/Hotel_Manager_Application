package org.example.menu;

import org.example.dao.BookingPaymentDAO;
import org.example.models.BookingPayment;
import org.example.models.Payment;
import org.example.models.Booking;
import org.example.utilities.InputHelper;
import java.sql.SQLException;
import java.util.List;

public class BookingPaymentMenu {

    public static void create(BookingPaymentDAO dao) throws SQLException {
        System.out.println("\n--- Добавление связи бронирование-платеж ---");
        int bookingId = InputHelper.readInt("ID бронирования: ");
        int paymentId = InputHelper.readInt("ID платежа: ");
        if (!dao.exists(bookingId, paymentId)) {
            BookingPayment relation = new BookingPayment(bookingId, paymentId);
            dao.create(relation);
            System.out.println("Связь создана! ID: " + relation.getId());
        } else {
            System.out.println("Такая связь уже существует!");
        }
    }

    public static void findAll(BookingPaymentDAO dao) throws SQLException {
        System.out.println("\n--- Все связи бронирование-платеж ---");
        List<BookingPayment> relations = dao.findAll();
        if (relations.isEmpty()) System.out.println("Связей не найдено");
        else { relations.forEach(System.out::println); System.out.println("Всего: " + relations.size()); }
    }

    public static void findById(BookingPaymentDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        BookingPayment relation = dao.findById(id);
        if (relation != null) System.out.println(relation);
        else System.out.println("Связь не найдена!");
    }

    public static void update(BookingPaymentDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        BookingPayment relation = dao.findById(id);
        if (relation == null) { System.out.println("Связь не найдена!"); return; }
        System.out.println("Текущие данные: " + relation);

        int newBookingId = InputHelper.readOptionalInt("Новый ID бронирования [" + relation.getBookingId() + "]: ", relation.getBookingId());
        int newPaymentId = InputHelper.readOptionalInt("Новый ID платежа [" + relation.getPaymentId() + "]: ", relation.getPaymentId());

        if (newBookingId != relation.getBookingId() || newPaymentId != relation.getPaymentId()) {
            relation.setBookingId(newBookingId);
            relation.setPaymentId(newPaymentId);
            dao.update(relation);
            System.out.println("Связь обновлена!");
        } else {
            System.out.println("Изменений не было.");
        }
    }

    public static void delete(BookingPaymentDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        BookingPayment relation = dao.findById(id);
        if (relation == null) { System.out.println("Связь не найдена!"); return; }
        if (InputHelper.confirmAction("Удалить связь " + relation)) {
            dao.delete(id);
            System.out.println("Связь удалена!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    public static void showPaymentsByBooking(BookingPaymentDAO dao) throws SQLException {
        int bookingId = InputHelper.readInt("Введите ID бронирования: ");
        System.out.println("\n--- Платежи бронирования ---");
        List<Payment> payments = dao.getPaymentsByBookingId(bookingId);
        if (payments.isEmpty()) System.out.println("Платежи не найдены");
        else { payments.forEach(System.out::println); System.out.println("Всего: " + payments.size()); }
    }

    public static void showBookingsByPayment(BookingPaymentDAO dao) throws SQLException {
        int paymentId = InputHelper.readInt("Введите ID платежа: ");
        System.out.println("\n--- Бронирования платежа ---");
        List<Booking> bookings = dao.getBookingsByPaymentId(paymentId);
        if (bookings.isEmpty()) System.out.println("Бронирования не найдены");
        else { bookings.forEach(System.out::println); System.out.println("Всего: " + bookings.size()); }
    }

    public static void deleteByBookingId(BookingPaymentDAO dao) throws SQLException {
        int bookingId = InputHelper.readInt("Введите ID бронирования: ");
        if (InputHelper.confirmAction("Удалить все связи бронирования")) {
            dao.deleteByBookingId(bookingId);
            System.out.println("Все связи бронирования удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    public static void deleteByPaymentId(BookingPaymentDAO dao) throws SQLException {
        int paymentId = InputHelper.readInt("Введите ID платежа: ");
        if (InputHelper.confirmAction("Удалить все связи платежа")) {
            dao.deleteByPaymentId(paymentId);
            System.out.println("Все связи платежа удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }
}