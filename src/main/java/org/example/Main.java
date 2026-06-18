package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.*;
import org.example.menu.*;
import org.example.db.DatabaseConnection;
import org.example.utilities.InputHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import static org.example.utilities.CrudUtils.*;

@Slf4j
public class Main {

    private static final String ENTITY_GUEST = "Гость";
    private static final String ENTITY_ROOM = "Номер";
    private static final String ENTITY_BOOKING = "Бронирование";
    private static final String ENTITY_EMPLOYEE = "Сотрудник";
    private static final String ENTITY_SERVICE = "Услуга";
    private static final String ENTITY_POSITION = "Должность";
    private static final String ENTITY_PAYMENT = "Платеж";

    private static final String RELATION_EMPLOYEE_POSITION = "Сотрудник ↔ Должность";
    private static final String RELATION_ROOM_BOOKING = "Номер ↔ Бронирование";
    private static final String RELATION_BOOKING_PAYMENT = "Бронирование ↔ Платеж";
    private static final String RELATION_ROOM_EMPLOYEE = "Номер ↔ Сотрудник";
    private static final String RELATION_SERVICE_EMPLOYEE = "Услуга ↔ Сотрудник";
    private static final String RELATION_SERVICE_GUEST = "Услуга ↔ Гость";
    private static final String RELATION_SERVICE_PAYMENT = "Услуга ↔ Платеж";

    private static final String MSG_INVALID_CHOICE = "Неверный выбор!";

    private static GuestDAO guestDAO;
    private static RoomDAO roomDAO;
    private static BookingDAO bookingDAO;
    private static EmployeeDAO employeeDAO;
    private static ServiceDAO serviceDAO;
    private static PositionDAO positionDAO;
    private static PaymentDAO paymentDAO;

    private static EmployeePositionDAO employeePositionDAO;
    private static RoomBookingDAO roomBookingDAO;
    private static BookingPaymentDAO bookingPaymentDAO;
    private static RoomEmployeeDAO roomEmployeeDAO;
    private static ServiceEmployeeDAO serviceEmployeeDAO;
    private static ServiceGuestDAO serviceGuestDAO;
    private static ServicePaymentDAO servicePaymentDAO;

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {

            guestDAO = new GuestDAO(connection);
            roomDAO = new RoomDAO(connection);
            bookingDAO = new BookingDAO(connection);
            employeeDAO = new EmployeeDAO(connection);
            serviceDAO = new ServiceDAO(connection);
            positionDAO = new PositionDAO(connection);
            paymentDAO = new PaymentDAO(connection);

            employeePositionDAO = new EmployeePositionDAO(connection);
            roomBookingDAO = new RoomBookingDAO(connection);
            bookingPaymentDAO = new BookingPaymentDAO(connection);
            roomEmployeeDAO = new RoomEmployeeDAO(connection);
            serviceEmployeeDAO = new ServiceEmployeeDAO(connection);
            serviceGuestDAO = new ServiceGuestDAO(connection);
            servicePaymentDAO = new ServicePaymentDAO(connection);

            log.info("Подключение к БД успешно!");

            while (true) {
                printMainMenu();
                int choice = InputHelper.readMenuChoice("");

                switch (choice) {
                    case 1 -> guestMenu();
                    case 2 -> roomMenu();
                    case 3 -> bookingMenu();
                    case 4 -> employeeMenu();
                    case 5 -> serviceMenu();
                    case 6 -> positionMenu();
                    case 7 -> paymentMenu();
                    case 8 -> employeePositionMenu();
                    case 9 -> roomBookingMenu();
                    case 10 -> bookingPaymentMenu();
                    case 11 -> roomEmployeeMenu();
                    case 12 -> serviceEmployeeMenu();
                    case 13 -> serviceGuestMenu();
                    case 14 -> servicePaymentMenu();
                    case 0 -> {
                        System.exit(0);
                    }
                    default -> log.warn(MSG_INVALID_CHOICE);
                }
            }
        } catch (SQLException e) {
            log.error("Ошибка подключения к БД: {}", e.getMessage(), e);
        }
    }

    private static void printMainMenu() {
        log.info("\n========== ГЛАВНОЕ МЕНЮ ==========");
        log.info("1. {}", ENTITY_GUEST );
        log.info("2. {}", ENTITY_ROOM);
        log.info("3. {}", ENTITY_BOOKING);
        log.info("4. {}", ENTITY_EMPLOYEE);
        log.info("5. {}", ENTITY_SERVICE );
        log.info("6. {}", ENTITY_POSITION);
        log.info("7. {}", ENTITY_PAYMENT);
        log.info("8. {}", RELATION_EMPLOYEE_POSITION);
        log.info("9. {}", RELATION_ROOM_BOOKING);
        log.info("10. {}", RELATION_BOOKING_PAYMENT);
        log.info("11. {}", RELATION_ROOM_EMPLOYEE);
        log.info("12. {}", RELATION_SERVICE_EMPLOYEE);
        log.info("13. {}", RELATION_SERVICE_GUEST);
        log.info("14. {}", RELATION_SERVICE_PAYMENT);
        log.info("0. Выход");
    }

    private static void guestMenu() throws SQLException {
        while (true) {
            printEntityMenu(ENTITY_GUEST);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");

            switch (choice) {
                case 1 -> createEntity(guestDAO, ENTITY_GUEST, GuestMenu::createGuest);
                case 2 -> findAllEntities(guestDAO, ENTITY_GUEST);
                case 3 -> findEntityById(guestDAO, ENTITY_GUEST);
                case 4 -> updateEntity(guestDAO, ENTITY_GUEST, GuestMenu::updateGuest);
                case 5 -> deleteEntity(guestDAO, ENTITY_GUEST);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void roomMenu() throws SQLException {
        while (true) {
            printEntityMenu(ENTITY_ROOM);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");

            switch (choice) {
                case 1 -> createEntity(roomDAO, ENTITY_ROOM, RoomMenu::createRoom);
                case 2 -> findAllEntities(roomDAO, ENTITY_ROOM);
                case 3 -> findEntityById(roomDAO, ENTITY_ROOM);
                case 4 -> updateEntity(roomDAO, ENTITY_ROOM, RoomMenu::updateRoom);
                case 5 -> deleteEntity(roomDAO, ENTITY_ROOM);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void bookingMenu() throws SQLException {
        while (true) {
            printEntityMenu(ENTITY_BOOKING);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");

            switch (choice) {
                case 1 -> createEntity(bookingDAO, ENTITY_BOOKING, BookingMenu::createBooking);
                case 2 -> findAllEntities(bookingDAO, ENTITY_BOOKING);
                case 3 -> findEntityById(bookingDAO, ENTITY_BOOKING);
                case 4 -> updateEntity(bookingDAO, ENTITY_BOOKING, BookingMenu::updateBooking);
                case 5 -> deleteEntity(bookingDAO, ENTITY_BOOKING);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void employeeMenu() throws SQLException {
        while (true) {
            printEntityMenu(ENTITY_EMPLOYEE);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");

            switch (choice) {
                case 1 -> createEntity(employeeDAO, ENTITY_EMPLOYEE, EmployeeMenu::createEmployee);
                case 2 -> findAllEntities(employeeDAO, ENTITY_EMPLOYEE);
                case 3 -> findEntityById(employeeDAO, ENTITY_EMPLOYEE);
                case 4 -> updateEntity(employeeDAO, ENTITY_EMPLOYEE, EmployeeMenu::updateEmployee);
                case 5 -> deleteEntity(employeeDAO, ENTITY_EMPLOYEE);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void serviceMenu() throws SQLException {
        while (true) {
            printEntityMenu(ENTITY_SERVICE);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");

            switch (choice) {
                case 1 -> createEntity(serviceDAO, ENTITY_SERVICE, ServiceMenu::createService);
                case 2 -> findAllEntities(serviceDAO, ENTITY_SERVICE);
                case 3 -> findEntityById(serviceDAO, ENTITY_SERVICE);
                case 4 -> updateEntity(serviceDAO, ENTITY_SERVICE, ServiceMenu::updateService);
                case 5 -> deleteEntity(serviceDAO, ENTITY_SERVICE);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void positionMenu() throws SQLException {
        while (true) {
            printEntityMenu(ENTITY_POSITION);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");

            switch (choice) {
                case 1 -> createEntity(positionDAO, ENTITY_POSITION, PositionMenu::createPosition);
                case 2 -> findAllEntities(positionDAO, ENTITY_POSITION);
                case 3 -> findEntityById(positionDAO, ENTITY_POSITION);
                case 4 -> updateEntity(positionDAO, ENTITY_POSITION, PositionMenu::updatePosition);
                case 5 -> deleteEntity(positionDAO, ENTITY_POSITION);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void paymentMenu() throws SQLException {
        while (true) {
            printEntityMenu(ENTITY_PAYMENT);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");

            switch (choice) {
                case 1 -> createEntity(paymentDAO, ENTITY_PAYMENT, PaymentMenu::createPayment);
                case 2 -> findAllEntities(paymentDAO, ENTITY_PAYMENT);
                case 3 -> findEntityById(paymentDAO, ENTITY_PAYMENT);
                case 4 -> updateEntity(paymentDAO, ENTITY_PAYMENT, PaymentMenu::updatePayment);
                case 5 -> deleteEntity(paymentDAO, ENTITY_PAYMENT);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void employeePositionMenu() throws SQLException {
        while (true) {
            printRelationMenu(RELATION_EMPLOYEE_POSITION);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");
            switch (choice) {
                case 1 -> EmployeePositionMenu.create(employeePositionDAO);
                case 2 -> EmployeePositionMenu.findAll(employeePositionDAO);
                case 3 -> EmployeePositionMenu.findById(employeePositionDAO);
                case 4 -> EmployeePositionMenu.update(employeePositionDAO);
                case 5 -> EmployeePositionMenu.delete(employeePositionDAO);
                case 6 -> EmployeePositionMenu.showPositionsByEmployee(employeePositionDAO);
                case 7 -> EmployeePositionMenu.showEmployeesByPosition(employeePositionDAO);
                case 8 -> EmployeePositionMenu.deleteByEmployeeId(employeePositionDAO);
                case 9 -> EmployeePositionMenu.deleteByPositionId(employeePositionDAO);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void roomBookingMenu() throws SQLException {
        while (true) {
            printRelationMenu(RELATION_ROOM_BOOKING);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");
            switch (choice) {
                case 1 -> RoomBookingMenu.create(roomBookingDAO);
                case 2 -> RoomBookingMenu.findAll(roomBookingDAO);
                case 3 -> RoomBookingMenu.findById(roomBookingDAO);
                case 4 -> RoomBookingMenu.update(roomBookingDAO);
                case 5 -> RoomBookingMenu.delete(roomBookingDAO);
                case 6 -> RoomBookingMenu.showRoomsByBooking(roomBookingDAO);
                case 7 -> RoomBookingMenu.showBookingsByRoom(roomBookingDAO);
                case 8 -> RoomBookingMenu.deleteByRoomId(roomBookingDAO);
                case 9 -> RoomBookingMenu.deleteByBookingId(roomBookingDAO);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void bookingPaymentMenu() throws SQLException {
        while (true) {
            printRelationMenu(RELATION_BOOKING_PAYMENT);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");
            switch (choice) {
                case 1 -> BookingPaymentMenu.create(bookingPaymentDAO);
                case 2 -> BookingPaymentMenu.findAll(bookingPaymentDAO);
                case 3 -> BookingPaymentMenu.findById(bookingPaymentDAO);
                case 4 -> BookingPaymentMenu.update(bookingPaymentDAO);
                case 5 -> BookingPaymentMenu.delete(bookingPaymentDAO);
                case 6 -> BookingPaymentMenu.showPaymentsByBooking(bookingPaymentDAO);
                case 7 -> BookingPaymentMenu.showBookingsByPayment(bookingPaymentDAO);
                case 8 -> BookingPaymentMenu.deleteByBookingId(bookingPaymentDAO);
                case 9 -> BookingPaymentMenu.deleteByPaymentId(bookingPaymentDAO);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void roomEmployeeMenu() throws SQLException {
        while (true) {
            printRelationMenu(RELATION_ROOM_EMPLOYEE);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");
            switch (choice) {
                case 1 -> RoomEmployeeMenu.create(roomEmployeeDAO);
                case 2 -> RoomEmployeeMenu.findAll(roomEmployeeDAO);
                case 3 -> RoomEmployeeMenu.findById(roomEmployeeDAO);
                case 4 -> RoomEmployeeMenu.update(roomEmployeeDAO);
                case 5 -> RoomEmployeeMenu.delete(roomEmployeeDAO);
                case 6 -> RoomEmployeeMenu.showEmployeesByRoom(roomEmployeeDAO);
                case 7 -> RoomEmployeeMenu.showRoomsByEmployee(roomEmployeeDAO);
                case 8 -> RoomEmployeeMenu.deleteByRoomId(roomEmployeeDAO);
                case 9 -> RoomEmployeeMenu.deleteByEmployeeId(roomEmployeeDAO);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void serviceEmployeeMenu() throws SQLException {
        while (true) {
            printRelationMenu(RELATION_SERVICE_EMPLOYEE);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");
            switch (choice) {
                case 1 -> ServiceEmployeeMenu.create(serviceEmployeeDAO);
                case 2 -> ServiceEmployeeMenu.findAll(serviceEmployeeDAO);
                case 3 -> ServiceEmployeeMenu.findById(serviceEmployeeDAO);
                case 4 -> ServiceEmployeeMenu.update(serviceEmployeeDAO);
                case 5 -> ServiceEmployeeMenu.delete(serviceEmployeeDAO);
                case 6 -> ServiceEmployeeMenu.showEmployeesByService(serviceEmployeeDAO);
                case 7 -> ServiceEmployeeMenu.showServicesByEmployee(serviceEmployeeDAO);
                case 8 -> ServiceEmployeeMenu.deleteByServiceId(serviceEmployeeDAO);
                case 9 -> ServiceEmployeeMenu.deleteByEmployeeId(serviceEmployeeDAO);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void serviceGuestMenu() throws SQLException {
        while (true) {
            printRelationMenu(RELATION_SERVICE_GUEST);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");
            switch (choice) {
                case 1 -> ServiceGuestMenu.create(serviceGuestDAO);
                case 2 -> ServiceGuestMenu.findAll(serviceGuestDAO);
                case 3 -> ServiceGuestMenu.findById(serviceGuestDAO);
                case 4 -> ServiceGuestMenu.update(serviceGuestDAO);
                case 5 -> ServiceGuestMenu.delete(serviceGuestDAO);
                case 6 -> ServiceGuestMenu.showGuestsByService(serviceGuestDAO);
                case 7 -> ServiceGuestMenu.showServicesByGuest(serviceGuestDAO);
                case 8 -> ServiceGuestMenu.deleteByServiceId(serviceGuestDAO);
                case 9 -> ServiceGuestMenu.deleteByGuestId(serviceGuestDAO);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void servicePaymentMenu() throws SQLException {
        while (true) {
            printRelationMenu(RELATION_SERVICE_PAYMENT);
            int choice = InputHelper.readMenuChoice("Ваш выбор: ");
            switch (choice) {
                case 1 -> ServicePaymentMenu.create(servicePaymentDAO);
                case 2 -> ServicePaymentMenu.findAll(servicePaymentDAO);
                case 3 -> ServicePaymentMenu.findById(servicePaymentDAO);
                case 4 -> ServicePaymentMenu.update(servicePaymentDAO);
                case 5 -> ServicePaymentMenu.delete(servicePaymentDAO);
                case 6 -> ServicePaymentMenu.showPaymentsByService(servicePaymentDAO);
                case 7 -> ServicePaymentMenu.showServicesByPayment(servicePaymentDAO);
                case 8 -> ServicePaymentMenu.deleteByServiceId(servicePaymentDAO);
                case 9 -> ServicePaymentMenu.deleteByPaymentId(servicePaymentDAO);
                case 0 -> { return; }
                default -> log.warn(MSG_INVALID_CHOICE);
            }
        }
    }

    private static void printEntityMenu(String entityName) {
        log.info("\n========== {} ==========", entityName.toUpperCase());
        log.info("1. Добавить");
        log.info("2. Показать все");
        log.info("3. Найти по ID");
        log.info("4. Обновить");
        log.info("5. Удалить");
        log.info("0. Назад");
    }

    private static void printRelationMenu(String relationName) {
        log.info("\n========== {} ==========", relationName);
        log.info("1. Добавить связь");
        log.info("2. Показать все связи");
        log.info("3. Найти связь по ID");
        log.info("4. Обновить связь");
        log.info("5. Удалить связь");
        log.info("6. Показать по правой связи");
        log.info("7. Показать по левой связи");
        log.info("8. Удалить по правой связи");
        log.info("9. Удалить по левой связи");
        log.info("0. Назад");
    }
}