package org.example.menu;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.ServiceGuestDAO;
import org.example.models.ServiceGuest;
import org.example.models.Guest;
import org.example.models.Service;
import org.example.utilities.InputHelper;

import java.sql.SQLException;
import java.util.List;

import static org.example.menu.MenuConstants.*;

@Slf4j
public class ServiceGuestMenu {

    private ServiceGuestMenu() {}

    public static void create(ServiceGuestDAO dao) throws SQLException {
        log.info("\n--- Добавление связи услуга-гость ---");
        int serviceId = InputHelper.readInt("ID услуги: ");
        int guestId = InputHelper.readInt("ID гостя: ");
        if (!dao.exists(serviceId, guestId)) {
            ServiceGuest relation = new ServiceGuest(serviceId, guestId);
            dao.create(relation);
            log.info(MSG_RELATION_CREATED + relation.getId());
        } else {
            log.warn(MSG_RELATION_EXISTS);
        }
    }

    public static void findAll(ServiceGuestDAO dao) throws SQLException {
        log.info("\n--- Все связи услуга-гость ---");
        List<ServiceGuest> relations = dao.findAll();
        if (relations.isEmpty()) {
            log.info(MSG_NO_RELATIONS);
        } else {
            relations.forEach(relation -> log.info(relation.toString()));
            log.info(MSG_TOTAL + relations.size());
        }
    }

    public static void findById(ServiceGuestDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        ServiceGuest relation = dao.findById(id);
        if (relation != null) {
            log.info(relation.toString());
        } else {
            log.warn(MSG_NOT_FOUND);
        }
    }

    public static void update(ServiceGuestDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        ServiceGuest relation = dao.findById(id);
        if (relation == null) {
            log.warn(MSG_NOT_FOUND);
            return;
        }
        log.info("Текущие данные: {}", relation);
        int newServiceId = InputHelper.readOptionalInt("Новый ID услуги [" + relation.getServiceId() + "]: ", relation.getServiceId());
        int newGuestId = InputHelper.readOptionalInt("Новый ID гостя [" + relation.getGuestId() + "]: ", relation.getGuestId());
        if (newServiceId != relation.getServiceId() || newGuestId != relation.getGuestId()) {
            relation.setServiceId(newServiceId);
            relation.setGuestId(newGuestId);
            dao.update(relation);
            log.info(MSG_UPDATED);
        } else {
            log.info(MSG_NO_CHANGES);
        }
    }

    public static void delete(ServiceGuestDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        ServiceGuest relation = dao.findById(id);
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

    public static void showGuestsByService(ServiceGuestDAO dao) throws SQLException {
        int serviceId = InputHelper.readInt("Введите ID услуги: ");
        log.info("\n--- Гости услуги ---");
        List<Guest> guests = dao.getGuestsByServiceId(serviceId);
        if (guests.isEmpty()) {
            log.info("Гости не найдены");
        } else {
            guests.forEach(guest -> log.info(guest.toString()));
            log.info(MSG_TOTAL + guests.size());
        }
    }

    public static void showServicesByGuest(ServiceGuestDAO dao) throws SQLException {
        int guestId = InputHelper.readInt("Введите ID гостя: ");
        log.info("\n--- Услуги гостя ---");
        List<Service> services = dao.getServicesByGuestId(guestId);
        if (services.isEmpty()) {
            log.info("Услуги не найдены");
        } else {
            services.forEach(service -> log.info(service.toString()));
            log.info(MSG_TOTAL + services.size());
        }
    }
}