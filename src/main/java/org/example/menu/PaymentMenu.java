package org.example.menu;

import org.example.models.Payment;
import org.example.utilities.InputHelper;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentMenu {

    public static Payment createPayment() {
        System.out.println("\n--- Добавление нового платежа ---");
        String status = InputHelper.readNonEmptyString("Статус (оплачено/не оплачено): ");
        BigDecimal amount = InputHelper.readBigDecimal("Сумма: ");
        LocalDate date = InputHelper.readLocalDate("Дата (yyyy-MM-dd): ");
        String method = InputHelper.readNonEmptyString("Метод оплаты (наличные/карта): ");
        int guestId = InputHelper.readInt("ID гостя: ");
        return new Payment(status, amount, date, method, guestId);
    }

    public static boolean updatePayment(Payment payment) {
        boolean updated = false;
        System.out.println("Текущие данные: " + payment);
        System.out.println("(оставьте поле пустым, чтобы не менять)");

        String status = InputHelper.readOptionalString("Новый статус [" + payment.getStatus() + "]: ");
        if (!status.isEmpty()) {
            payment.setStatus(status);
            updated = true;
        }

        BigDecimal newAmount = InputHelper.readOptionalBigDecimal("Новая сумма [" + payment.getAmount() + "]: ", payment.getAmount());
        if (!newAmount.equals(payment.getAmount())) {
            payment.setAmount(newAmount);
            updated = true;
        }

        LocalDate newDate = InputHelper.readOptionalLocalDate("Новая дата [" + payment.getPaymentDate() + "]: ", payment.getPaymentDate());
        if (!newDate.equals(payment.getPaymentDate())) {
            payment.setPaymentDate(newDate);
            updated = true;
        }

        String method = InputHelper.readOptionalString("Новый метод [" + payment.getPaymentMethod() + "]: ");
        if (!method.isEmpty()) {
            payment.setPaymentMethod(method);
            updated = true;
        }

        int newGuestId = InputHelper.readOptionalInt("Новый ID гостя [" + payment.getGuestId() + "]: ", payment.getGuestId());
        if (newGuestId != payment.getGuestId()) {
            payment.setGuestId(newGuestId);
            updated = true;
        }
        return updated;
    }
}