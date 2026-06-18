package org.example.menu;

import org.example.dao.ServicePaymentDAO;
import org.example.models.ServicePayment;
import org.example.models.Payment;
import org.example.models.Service;
import org.example.utilities.InputHelper;
import java.sql.SQLException;
import java.util.List;

public class ServicePaymentMenu {

    public static void create(ServicePaymentDAO dao) throws SQLException {
        System.out.println("\n--- Добавление связи услуга-платеж ---");
        int serviceId = InputHelper.readInt("ID услуги: ");
        int paymentId = InputHelper.readInt("ID платежа: ");
        if (!dao.exists(serviceId, paymentId)) {
            ServicePayment relation = new ServicePayment(serviceId, paymentId);
            dao.create(relation);
            System.out.println("Связь создана! ID: " + relation.getId());
        } else {
            System.out.println("Такая связь уже существует!");
        }
    }

    public static void findAll(ServicePaymentDAO dao) throws SQLException {
        System.out.println("\n--- Все связи услуга-платеж ---");
        List<ServicePayment> relations = dao.findAll();
        if (relations.isEmpty()) {
            System.out.println("Связей не найдено");
        } else {
            relations.forEach(System.out::println);
            System.out.println("Всего: " + relations.size());
        }
    }

    public static void findById(ServicePaymentDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи: ");
        ServicePayment relation = dao.findById(id);
        if (relation != null) {
            System.out.println(relation);
        } else {
            System.out.println("Связь не найдена!");
        }
    }

    public static void update(ServicePaymentDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для обновления: ");
        ServicePayment relation = dao.findById(id);
        if (relation == null) {
            System.out.println("Связь не найдена!");
            return;
        }
        System.out.println("Текущие данные: " + relation);

        int newServiceId = InputHelper.readOptionalInt("Новый ID услуги [" + relation.getServiceId() + "]: ", relation.getServiceId());
        int newPaymentId = InputHelper.readOptionalInt("Новый ID платежа [" + relation.getPaymentId() + "]: ", relation.getPaymentId());

        if (newServiceId != relation.getServiceId() || newPaymentId != relation.getPaymentId()) {
            relation.setServiceId(newServiceId);
            relation.setPaymentId(newPaymentId);
            dao.update(relation);
            System.out.println("Связь обновлена!");
        } else {
            System.out.println("Изменений не было.");
        }
    }

    public static void delete(ServicePaymentDAO dao) throws SQLException {
        int id = InputHelper.readInt("Введите ID связи для удаления: ");
        ServicePayment relation = dao.findById(id);
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

    public static void showPaymentsByService(ServicePaymentDAO dao) throws SQLException {
        int serviceId = InputHelper.readInt("Введите ID услуги: ");
        System.out.println("\n--- Платежи услуги ---");
        List<Payment> payments = dao.getPaymentsByServiceId(serviceId);
        if (payments.isEmpty()) {
            System.out.println("Платежи не найдены");
        } else {
            payments.forEach(System.out::println);
            System.out.println("Всего: " + payments.size());
        }
    }

    public static void showServicesByPayment(ServicePaymentDAO dao) throws SQLException {
        int paymentId = InputHelper.readInt("Введите ID платежа: ");
        System.out.println("\n--- Услуги платежа ---");
        List<Service> services = dao.getServicesByPaymentId(paymentId);
        if (services.isEmpty()) {
            System.out.println("Услуги не найдены");
        } else {
            services.forEach(System.out::println);
            System.out.println("Всего: " + services.size());
        }
    }

    public static void deleteByServiceId(ServicePaymentDAO dao) throws SQLException {
        int serviceId = InputHelper.readInt("Введите ID услуги: ");
        if (InputHelper.confirmAction("Удалить все связи услуги")) {
            dao.deleteByServiceId(serviceId);
            System.out.println("Все связи услуги удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    public static void deleteByPaymentId(ServicePaymentDAO dao) throws SQLException {
        int paymentId = InputHelper.readInt("Введите ID платежа: ");
        if (InputHelper.confirmAction("Удалить все связи платежа")) {
            dao.deleteByPaymentId(paymentId);
            System.out.println("Все связи платежа удалены!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }
}