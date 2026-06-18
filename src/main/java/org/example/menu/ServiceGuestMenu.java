package org.example.menu;

import org.example.dao.ServiceGuestDAO;
import org.example.models.ServiceGuest;
import org.example.models.Guest;
import org.example.models.Service;
import org.example.utilities.InputHelper;
import java.sql.SQLException;
import java.util.List;

public class ServiceGuestMenu {

    public static void create(ServiceGuestDAO dao) throws SQLException {
        System.out.println("\n--- Добавление связи услуга-гость ---");
        int serviceId = InputHelper.readInt("ID услуги: ");
        int guestId = InputHelper.readInt("ID гостя: ");
        if (!dao.exists(serviceId, guestId)) {
            ServiceGuest relation = new ServiceGuest(serviceId, guestId);
            dao.create(relation);
            System.out.println("Связь создана! ID: " + relation.getId());
        } else {
            System.out.println("Такая связь уже существует!");
        }
    }

    public static void findAll(ServiceGuestDAO dao) throws SQLException {
        System.out.println("\n--- Все связи услуга-гость ---");
        List<ServiceGuest> relations = dao.findAll();
        if (relations.isEmpty()) {
            System.out.println("Связей не найдено");
        } else {
            relations.forEach(System.out::println);
            System.out.println("Всего: " + relations.size());
        }
    }

    public static void findById(ServiceGuestDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        ServiceGuest relation = dao.findById(id);
        if (relation != null) {
            System.out.println(relation);
        } else {
            System.out.println("Связь не найдена!");
        }
    }

    public static void update(ServiceGuestDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        ServiceGuest relation = dao.findById(id);
        if (relation == null) {
            System.out.println("Связь не найдена!");
            return;
        }
        System.out.println("Текущие данные: " + relation);

        int newServiceId = InputHelper.readOptionalInt("Новый ID услуги [" + relation.getServiceId() + "]: ", relation.getServiceId());
        int newGuestId = InputHelper.readOptionalInt("Новый ID гостя [" + relation.getGuestId() + "]: ", relation.getGuestId());

        if (newServiceId != relation.getServiceId() || newGuestId != relation.getGuestId()) {
            relation.setServiceId(newServiceId);
            relation.setGuestId(newGuestId);
            dao.update(relation);
            System.out.println("Связь обновлена!");
        } else {
            System.out.println("Изменений не было.");
        }
    }

    public static void delete(ServiceGuestDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        ServiceGuest relation = dao.findById(id);
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

    public static void showGuestsByService(ServiceGuestDAO dao) throws SQLException {
        int serviceId = InputHelper.readInt("Введите ID услуги: ");
        System.out.println("\n--- Гости услуги ---");
        List<Guest> guests = dao.getGuestsByServiceId(serviceId);
        if (guests.isEmpty()) {
            System.out.println("Гости не найдены");
        } else {
            guests.forEach(System.out::println);
            System.out.println("Всего: " + guests.size());
        }
    }

    public static void showServicesByGuest(ServiceGuestDAO dao) throws SQLException {
        int guestId = InputHelper.readInt("Введите ID гостя: ");
        System.out.println("\n--- Услуги гостя ---");
        List<Service> services = dao.getServicesByGuestId(guestId);
        if (services.isEmpty()) {
            System.out.println("Услуги не найдены");
        } else {
            services.forEach(System.out::println);
            System.out.println("Всего: " + services.size());
        }
    }

    public static void deleteByServiceId(ServiceGuestDAO dao) throws SQLException {
        int serviceId = InputHelper.readInt("Введите ID услуги: ");
        if (InputHelper.confirmAction("Удалить все связи услуги")) {
            dao.deleteByServiceId(serviceId);
            System.out.println("Все связи услуги удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    public static void deleteByGuestId(ServiceGuestDAO dao) throws SQLException {
        int guestId = InputHelper.readInt("Введите ID гостя: ");
        if (InputHelper.confirmAction("Удалить все связи гостя")) {
            dao.deleteByGuestId(guestId);
            System.out.println("Все связи гостя удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }
}