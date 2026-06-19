package org.example.menu;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.RoomBookingDAO;
import org.example.models.RoomBooking;
import org.example.models.Room;
import org.example.models.Booking;
import org.example.utilities.InputHelper;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class RoomBookingMenu {

    private RoomBookingMenu() {}

    private static final String MSG_RELATION_EXISTS = "Такая связь уже существует!";
    private static final String MSG_RELATION_CREATED = "Связь создана! ID: ";
    private static final String MSG_NO_RELATIONS = "Связей не найдено";
    private static final String MSG_TOTAL = "Всего: ";
    private static final String MSG_NOT_FOUND = "Связь не найдена!";
    private static final String MSG_UPDATED = "Связь обновлена!";
    private static final String MSG_NO_CHANGES = "Изменений не было.";
    private static final String MSG_DELETED = "Связь удалена!";
    private static final String MSG_DELETE_CANCELLED = "Удаление отменено.";
    private static final String MSG_ROOMS_DELETED = "Все связи номера удалены!";
    private static final String MSG_BOOKINGS_DELETED = "Все связи бронирования удалены!";

    public static void create(RoomBookingDAO dao) throws SQLException {
        log.info("\n--- Добавление связи номер-бронирование ---");
        int roomId = InputHelper.readInt("ID номера: ");
        int bookingId = InputHelper.readInt("ID бронирования: ");
        if (!dao.exists(roomId, bookingId)) {
            RoomBooking relation = new RoomBooking(roomId, bookingId);
            dao.create(relation);
            log.info(MSG_RELATION_CREATED + relation.getId());
        } else {
            log.warn(MSG_RELATION_EXISTS);
        }
    }

    public static void findAll(RoomBookingDAO dao) throws SQLException {
        log.info("\n--- Все связи номер-бронирование ---");
        List<RoomBooking> relations = dao.findAll();
        if (relations.isEmpty()) {
            log.info(MSG_NO_RELATIONS);
        } else {
            relations.forEach(relation -> log.info(relation.toString()));
            log.info(MSG_TOTAL + relations.size());
        }
    }

    public static void findById(RoomBookingDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        RoomBooking relation = dao.findById(id);
        if (relation != null) {
            log.info(relation.toString());
        } else {
            log.warn(MSG_NOT_FOUND);
        }
    }

    public static void update(RoomBookingDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        RoomBooking relation = dao.findById(id);
        if (relation == null) {
            log.warn(MSG_NOT_FOUND);
            return;
        }
        log.info("Текущие данные: {}", relation);
        int newRoomId = InputHelper.readOptionalInt("Новый ID номера [" + relation.getRoomId() + "]: ", relation.getRoomId());
        int newBookingId = InputHelper.readOptionalInt("Новый ID бронирования [" + relation.getBookingId() + "]: ", relation.getBookingId());
        if (newRoomId != relation.getRoomId() || newBookingId != relation.getBookingId()) {
            relation.setRoomId(newRoomId);
            relation.setBookingId(newBookingId);
            dao.update(relation);
            log.info(MSG_UPDATED);
        } else {
            log.info(MSG_NO_CHANGES);
        }
    }

    public static void delete(RoomBookingDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        RoomBooking relation = dao.findById(id);
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

    public static void showRoomsByBooking(RoomBookingDAO dao) throws SQLException {
        int bookingId = InputHelper.readInt("Введите ID бронирования: ");
        log.info("\n--- Номера в бронировании ---");
        List<Room> rooms = dao.getRoomsByBookingId(bookingId);
        if (rooms.isEmpty()) {
            log.info("Номера не найдены");
        } else {
            rooms.forEach(room -> log.info(room.toString()));
            log.info(MSG_TOTAL + rooms.size());
        }
    }

    public static void showBookingsByRoom(RoomBookingDAO dao) throws SQLException {
        int roomId = InputHelper.readInt("Введите ID номера: ");
        log.info("\n--- Бронирования номера ---");
        List<Booking> bookings = dao.getBookingsByRoomId(roomId);
        if (bookings.isEmpty()) {
            log.info("Бронирования не найдены");
        } else {
            bookings.forEach(booking -> log.info(booking.toString()));
            log.info(MSG_TOTAL + bookings.size());
        }
    }

    public static void deleteByRoomId(RoomBookingDAO dao) throws SQLException {
        int roomId = InputHelper.readInt("Введите ID номера: ");
        if (InputHelper.confirmAction("Удалить все связи номера")) {
            dao.deleteByRoomId(roomId);
            log.info(MSG_ROOMS_DELETED);
        } else {
            log.info(MSG_DELETE_CANCELLED);
        }
    }

    public static void deleteByBookingId(RoomBookingDAO dao) throws SQLException {
        int bookingId = InputHelper.readInt("Введите ID бронирования: ");
        if (InputHelper.confirmAction("Удалить все связи бронирования")) {
            dao.deleteByBookingId(bookingId);
            log.info(MSG_BOOKINGS_DELETED);
        } else {
            log.info(MSG_DELETE_CANCELLED);
        }
    }
}