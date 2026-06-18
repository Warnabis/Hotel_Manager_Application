package org.example.menu;

import org.example.dao.RoomBookingDAO;
import org.example.models.RoomBooking;
import org.example.models.Room;
import org.example.models.Booking;
import org.example.utilities.InputHelper;
import java.sql.SQLException;
import java.util.List;

public class RoomBookingMenu {

    public static void create(RoomBookingDAO dao) throws SQLException {
        System.out.println("\n--- Добавление связи номер-бронирование ---");
        int roomId = InputHelper.readInt("ID номера: ");
        int bookingId = InputHelper.readInt("ID бронирования: ");
        if (!dao.exists(roomId, bookingId)) {
            RoomBooking relation = new RoomBooking(roomId, bookingId);
            dao.create(relation);
            System.out.println("Связь создана! ID: " + relation.getId());
        } else {
            System.out.println("Такая связь уже существует!");
        }
    }

    public static void findAll(RoomBookingDAO dao) throws SQLException {
        System.out.println("\n--- Все связи номер-бронирование ---");
        List<RoomBooking> relations = dao.findAll();
        if (relations.isEmpty()) {
            System.out.println("Связей не найдено");
        } else {
            relations.forEach(System.out::println);
            System.out.println("Всего: " + relations.size());
        }
    }

    public static void findById(RoomBookingDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        RoomBooking relation = dao.findById(id);
        if (relation != null) {
            System.out.println(relation);
        } else {
            System.out.println("Связь не найдена!");
        }
    }

    public static void update(RoomBookingDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        RoomBooking relation = dao.findById(id);
        if (relation == null) {
            System.out.println("Связь не найдена!");
            return;
        }
        System.out.println("Текущие данные: " + relation);

        int newRoomId = InputHelper.readOptionalInt("Новый ID номера [" + relation.getRoomId() + "]: ", relation.getRoomId());
        int newBookingId = InputHelper.readOptionalInt("Новый ID бронирования [" + relation.getBookingId() + "]: ", relation.getBookingId());

        if (newRoomId != relation.getRoomId() || newBookingId != relation.getBookingId()) {
            relation.setRoomId(newRoomId);
            relation.setBookingId(newBookingId);
            dao.update(relation);
            System.out.println("Связь обновлена!");
        } else {
            System.out.println("Изменений не было.");
        }
    }

    public static void delete(RoomBookingDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        RoomBooking relation = dao.findById(id);
        if (relation == null) {
            System.out.println("Связь не найдена!");
            return;
        }
        if (InputHelper.confirmAction("Удалить связь " + relation)) {
            dao.delete(id);
            System.out.println("Связь удалена!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    public static void showRoomsByBooking(RoomBookingDAO dao) throws SQLException {
        int bookingId = InputHelper.readInt("Введите ID бронирования: ");
        System.out.println("\n--- Номера в бронировании ---");
        List<Room> rooms = dao.getRoomsByBookingId(bookingId);
        if (rooms.isEmpty()) {
            System.out.println("Номера не найдены");
        } else {
            rooms.forEach(System.out::println);
            System.out.println("Всего: " + rooms.size());
        }
    }

    public static void showBookingsByRoom(RoomBookingDAO dao) throws SQLException {
        int roomId = InputHelper.readInt("Введите ID номера: ");
        System.out.println("\n--- Бронирования номера ---");
        List<Booking> bookings = dao.getBookingsByRoomId(roomId);
        if (bookings.isEmpty()) {
            System.out.println("Бронирования не найдены");
        } else {
            bookings.forEach(System.out::println);
            System.out.println("Всего: " + bookings.size());
        }
    }

    public static void deleteByRoomId(RoomBookingDAO dao) throws SQLException {
        int roomId = InputHelper.readInt("Введите ID номера: ");
        if (InputHelper.confirmAction("Удалить все связи номера")) {
            dao.deleteByRoomId(roomId);
            System.out.println("Все связи номера удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    public static void deleteByBookingId(RoomBookingDAO dao) throws SQLException {
        int bookingId = InputHelper.readInt("Введите ID бронирования: ");
        if (InputHelper.confirmAction("Удалить все связи бронирования")) {
            dao.deleteByBookingId(bookingId);
            System.out.println("Все связи бронирования удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }
}