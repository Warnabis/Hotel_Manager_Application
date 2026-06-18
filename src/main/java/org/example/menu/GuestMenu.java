package org.example.menu;

import org.example.models.Guest;
import org.example.utilities.InputHelper;

public class GuestMenu {

    public static Guest createGuest() {
        System.out.println("\n--- Добавление нового гостя ---");
        String fullName = InputHelper.readNonEmptyString("ФИО: ");
        String phoneNumber = InputHelper.readNonEmptyString("Телефон: ");
        String email = InputHelper.readNonEmptyString("Email: ");
        String status = InputHelper.readNonEmptyString("Статус (Зарегистрирован/Постоянный/VIP): ");
        return new Guest(fullName, phoneNumber, status, email);
    }

    public static boolean updateGuest(Guest guest) {
        boolean updated = false;
        System.out.println("Текущие данные: " + guest);
        System.out.println("(оставьте поле пустым, чтобы не менять)");

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