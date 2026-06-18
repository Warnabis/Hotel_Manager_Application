package org.example.menu;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.ServicePaymentDAO;
import org.example.models.ServicePayment;
import org.example.models.Payment;
import org.example.models.Service;
import org.example.utilities.InputHelper;

import java.sql.SQLException;
import java.util.List;

import static org.example.menu.MenuConstants.*;

@Slf4j
public class ServicePaymentMenu {

    private ServicePaymentMenu() {}

    public static void create(ServicePaymentDAO dao) throws SQLException {
        log.info("\n--- Добавление связи услуга-платеж ---");
        int serviceId = InputHelper.readInt("ID услуги: ");
        int paymentId = InputHelper.readInt("ID платежа: ");
        if (!dao.exists(serviceId, paymentId)) {
            ServicePayment relation = new ServicePayment(serviceId, paymentId);
            dao.create(relation);
            log.info(MSG_RELATION_CREATED + relation.getId());
        } else {
            log.warn(MSG_RELATION_EXISTS);
        }
    }

    public static void findAll(ServicePaymentDAO dao) throws SQLException {
        log.info("\n--- Все связи услуга-платеж ---");
        List<ServicePayment> relations = dao.findAll();
        if (relations.isEmpty()) {
            log.info(MSG_NO_RELATIONS);
        } else {
            relations.forEach(relation -> log.info(relation.toString()));
            log.info(MSG_TOTAL + relations.size());
        }
    }

    public static void findById(ServicePaymentDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        ServicePayment relation = dao.findById(id);
        if (relation != null) {
            log.info(relation.toString());
        } else {
            log.warn(MSG_NOT_FOUND);
        }
    }

    public static void update(ServicePaymentDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        ServicePayment relation = dao.findById(id);
        if (relation == null) {
            log.warn(MSG_NOT_FOUND);
            return;
        }
        log.info("Текущие данные: {}", relation);
        int newServiceId = InputHelper.readOptionalInt("Новый ID услуги [" + relation.getServiceId() + "]: ", relation.getServiceId());
        int newPaymentId = InputHelper.readOptionalInt("Новый ID платежа [" + relation.getPaymentId() + "]: ", relation.getPaymentId());
        if (newServiceId != relation.getServiceId() || newPaymentId != relation.getPaymentId()) {
            relation.setServiceId(newServiceId);
            relation.setPaymentId(newPaymentId);
            dao.update(relation);
            log.info(MSG_UPDATED);
        } else {
            log.info(MSG_NO_CHANGES);
        }
    }

    public static void delete(ServicePaymentDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        ServicePayment relation = dao.findById(id);
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

    public static void showPaymentsByService(ServicePaymentDAO dao) throws SQLException {
        int serviceId = InputHelper.readInt("Введите ID услуги: ");
        log.info("\n--- Платежи услуги ---");
        List<Payment> payments = dao.getPaymentsByServiceId(serviceId);
        if (payments.isEmpty()) {
            log.info("Платежи не найдены");
        } else {
            payments.forEach(payment -> log.info(payment.toString()));
            log.info(MSG_TOTAL + payments.size());
        }
    }

    public static void showServicesByPayment(ServicePaymentDAO dao) throws SQLException {
        int paymentId = InputHelper.readInt("Введите ID платежа: ");
        log.info("\n--- Услуги платежа ---");
        List<Service> services = dao.getServicesByPaymentId(paymentId);
        if (services.isEmpty()) {
            log.info("Услуги не найдены");
        } else {
            services.forEach(service -> log.info(service.toString()));
            log.info(MSG_TOTAL + services.size());
        }
    }
}