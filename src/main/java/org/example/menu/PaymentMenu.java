package org.example.menu;

import lombok.extern.slf4j.Slf4j;
import org.example.models.Payment;
import org.example.utilities.InputHelper;
import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
public class PaymentMenu {

    private PaymentMenu() {}

    public static Payment createPayment() {
        log.info("\n--- Добавление нового платежа ---");
        String status = InputHelper.readNonEmptyString("Статус оплаты: ");
        BigDecimal amount = InputHelper.readBigDecimal("Сумма: ");
        LocalDate date = InputHelper.readLocalDate("Дата (yyyy-MM-dd): ");
        String method = InputHelper.readNonEmptyString("Метод оплаты: ");
        int guestId = InputHelper.readInt("ID гостя: ");
        return new Payment(status, amount, date, method, guestId);
    }

    public static boolean updatePayment(Payment payment) {
        boolean updated = false;
        log.info("Текущие данные: {}", payment);
        log.info("(оставьте поле пустым, чтобы не менять)");

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

        LocalDate newDate = InputHelper.readOptionalLocalDate("Новая дата оплаты[" + payment.getPaymentDate() + "]: ", payment.getPaymentDate());
        if (!newDate.equals(payment.getPaymentDate())) {
            payment.setPaymentDate(newDate);
            updated = true;
        }

        String method = InputHelper.readOptionalString("Новый метод оплаты[" + payment.getPaymentMethod() + "]: ");
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