package org.example.menu;

import lombok.extern.slf4j.Slf4j;
import org.example.models.Guest;
import org.example.utilities.InputHelper;

@Slf4j
public class GuestMenu {

    private static final String MSG_ADD_GUEST = "--- Добавление нового гостя ---";
    private static final String PROMPT_FULL_NAME = "ФИО: ";
    private static final String PROMPT_PHONE = "Телефон: ";
    private static final String PROMPT_EMAIL = "Email: ";
    private static final String PROMPT_STATUS = "Статус: ";

    public static Guest createGuest() {
        log.info("\n{}", MSG_ADD_GUEST);
        String fullName = InputHelper.readNonEmptyString(PROMPT_FULL_NAME);
        String phoneNumber = InputHelper.readNonEmptyString(PROMPT_PHONE);
        String email = InputHelper.readNonEmptyString(PROMPT_EMAIL);
        String status = InputHelper.readNonEmptyString(PROMPT_STATUS);
        return new Guest(fullName, phoneNumber, status, email);
    }

    public static boolean updateGuest(Guest guest) {
        boolean updated = false;
        log.info("Текущие данные: {}", guest);
        log.info("(оставьте поле пустым, чтобы не менять)");

        String fullName = InputHelper.readOptionalString("Новое ФИО [" + guest.getFullName() + "]: ");
        if (!fullName.isEmpty()) {
            guest.setFullName(fullName);
            updated = true;
        }

        String phoneNumber = InputHelper.readOptionalString("Новый телефон [" + guest.getPhoneNumber() + "]: ");
        if (!phoneNumber.isEmpty()) {
            guest.setPhoneNumber(phoneNumber);
            updated = true;
        }

        String email = InputHelper.readOptionalString("Новый email [" + guest.getEmail() + "]: ");
        if (!email.isEmpty()) {
            guest.setEmail(email);
            updated = true;
        }

        String status = InputHelper.readOptionalString("Новый статус [" + guest.getStatus() + "]: ");
        if (!status.isEmpty()) {
            guest.setStatus(status);
            updated = true;
        }
        return updated;
    }
}