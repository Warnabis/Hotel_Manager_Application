package org.example;

import org.example.dao.*;
import org.example.menu.*;
import org.example.db.DatabaseConnection;
import org.example.utilities.InputHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import static org.example.utilities.CrudUtils.*;

public class Main {
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

            System.out.println("Подключение к БД успешно!");

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
                    default -> System.out.println("Неверный выбор!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printMainMenu() {
        System.out.println("\n========== ГЛАВНОЕ МЕНЮ ==========");
        System.out.println("--- Основные таблицы ---");
        System.out.println("1. Гости");
        System.out.println("2. Номера");
        System.out.println("3. Бронирования");
        System.out.println("4. Сотрудники");
        System.out.println("5. Услуги");
        System.out.println("6. Должности");
        System.out.println("7. Платежи");
        System.out.println("8. Сотрудник ↔ Должность");
        System.out.println("9. Номер ↔ Бронирование");
        System.out.println("10. Бронирование ↔ Платеж");
        System.out.println("11. Номер ↔ Сотрудник");
        System.out.println("12. Услуга ↔ Сотрудник");
        System.out.println("13. Услуга ↔ Гость");
        System.out.println("14. Услуга ↔ Платеж");
        System.out.println("0. Выход");
        System.out.print("Ваш выбор: ");
    }

    private static void guestMenu() throws SQLException {
        while (true) {
            printEntityMenu("Гость");
            int choice = InputHelper.readMenuChoice("");

            switch (choice) {
                case 1 -> createEntity(guestDAO, "Гость", GuestMenu::createGuest);
                case 2 -> findAllEntities(guestDAO, "Гость");
                case 3 -> findEntityById(guestDAO, "Гость");
                case 4 -> updateEntity(guestDAO, "Гость", GuestMenu::updateGuest);
                case 5 -> deleteEntity(guestDAO, "Гость");
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void roomMenu() throws SQLException {
        while (true) {
            printEntityMenu("Номер");
            int choice = InputHelper.readMenuChoice("");

            switch (choice) {
                case 1 -> createEntity(roomDAO, "Номер", RoomMenu::createRoom);
                case 2 -> findAllEntities(roomDAO, "Номер");
                case 3 -> findEntityById(roomDAO, "Номер");
                case 4 -> updateEntity(roomDAO, "Номер", RoomMenu::updateRoom);
                case 5 -> deleteEntity(roomDAO, "Номер");
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void bookingMenu() throws SQLException {
        while (true) {
            printEntityMenu("Бронирование");
            int choice = InputHelper.readMenuChoice("");

            switch (choice) {
                case 1 -> createEntity(bookingDAO, "Бронирование", BookingMenu::createBooking);
                case 2 -> findAllEntities(bookingDAO, "Бронирование");
                case 3 -> findEntityById(bookingDAO, "Бронирование");
                case 4 -> updateEntity(bookingDAO, "Бронирование", BookingMenu::updateBooking);
                case 5 -> deleteEntity(bookingDAO, "Бронирование");
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void employeeMenu() throws SQLException {
        while (true) {
            printEntityMenu("Сотрудник");
            int choice = InputHelper.readMenuChoice("");

            switch (choice) {
                case 1 -> createEntity(employeeDAO, "Сотрудник", EmployeeMenu::createEmployee);
                case 2 -> findAllEntities(employeeDAO, "Сотрудник");
                case 3 -> findEntityById(employeeDAO, "Сотрудник");
                case 4 -> updateEntity(employeeDAO, "Сотрудник", EmployeeMenu::updateEmployee);
                case 5 -> deleteEntity(employeeDAO, "Сотрудник");
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void serviceMenu() throws SQLException {
        while (true) {
            printEntityMenu("Услуга");
            int choice = InputHelper.readMenuChoice("");

            switch (choice) {
                case 1 -> createEntity(serviceDAO, "Услуга", ServiceMenu::createService);
                case 2 -> findAllEntities(serviceDAO, "Услуга");
                case 3 -> findEntityById(serviceDAO, "Услуга");
                case 4 -> updateEntity(serviceDAO, "Услуга", ServiceMenu::updateService);
                case 5 -> deleteEntity(serviceDAO, "Услуга");
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void positionMenu() throws SQLException {
        while (true) {
            printEntityMenu("Должность");
            int choice = InputHelper.readMenuChoice("");

            switch (choice) {
                case 1 -> createEntity(positionDAO, "Должность", PositionMenu::createPosition);
                case 2 -> findAllEntities(positionDAO, "Должность");
                case 3 -> findEntityById(positionDAO, "Должность");
                case 4 -> updateEntity(positionDAO, "Должность", PositionMenu::updatePosition);
                case 5 -> deleteEntity(positionDAO, "Должность");
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void paymentMenu() throws SQLException {
        while (true) {
            printEntityMenu("Платеж");
            int choice = InputHelper.readMenuChoice("");

            switch (choice) {
                case 1 -> createEntity(paymentDAO, "Платеж", PaymentMenu::createPayment);
                case 2 -> findAllEntities(paymentDAO, "Платеж");
                case 3 -> findEntityById(paymentDAO, "Платеж");
                case 4 -> updateEntity(paymentDAO, "Платеж", PaymentMenu::updatePayment);
                case 5 -> deleteEntity(paymentDAO, "Платеж");
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void employeePositionMenu() throws SQLException {
        while (true) {
            printRelationMenu("Сотрудник ↔ Должность");
            int choice = InputHelper.readMenuChoice("");
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
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void roomBookingMenu() throws SQLException {
        while (true) {
            printRelationMenu("Номер ↔ Бронирование");
            int choice = InputHelper.readMenuChoice("");
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
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void bookingPaymentMenu() throws SQLException {
        while (true) {
            printRelationMenu("Бронирование ↔ Платеж");
            int choice = InputHelper.readMenuChoice("");
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
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void roomEmployeeMenu() throws SQLException {
        while (true) {
            printRelationMenu("Номер ↔ Сотрудник");
            int choice = InputHelper.readMenuChoice("");
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
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void serviceEmployeeMenu() throws SQLException {
        while (true) {
            printRelationMenu("Услуга ↔ Сотрудник");
            int choice = InputHelper.readMenuChoice("");
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
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void serviceGuestMenu() throws SQLException {
        while (true) {
            printRelationMenu("Услуга ↔ Гость");
            int choice = InputHelper.readMenuChoice("");
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
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void servicePaymentMenu() throws SQLException {
        while (true) {
            printRelationMenu("Услуга ↔ Платеж");
            int choice = InputHelper.readMenuChoice("");
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
                default -> System.out.println("Неверный выбор!");
            }
        }
    }


    private static void printEntityMenu(String entityName) {
        System.out.println("\n========== " + entityName.toUpperCase() + " ==========");
        System.out.println("1. Добавить");
        System.out.println("2. Показать все");
        System.out.println("3. Найти по ID");
        System.out.println("4. Обновить");
        System.out.println("5. Удалить");
        System.out.println("0. Назад");
        System.out.print("Ваш выбор: ");
    }

    private static void printRelationMenu(String relationName) {
        System.out.println("\n========== " + relationName + " ==========");
        System.out.println("1. Добавить связь");
        System.out.println("2. Показать все связи");
        System.out.println("3. Найти связь по ID");
        System.out.println("4. Обновить связь");
        System.out.println("5. Удалить связь");
        System.out.println("6. Показать по правой связи");
        System.out.println("7. Показать по левой связи");
        System.out.println("8. Удалить по правой связи");
        System.out.println("9. Удалить по левой связи");
        System.out.println("0. Назад");
        System.out.print("Ваш выбор: ");
    }

}