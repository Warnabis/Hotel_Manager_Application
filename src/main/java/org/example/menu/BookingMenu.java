package org.example.menu;

import lombok.extern.slf4j.Slf4j;
import org.example.models.Booking;
import org.example.utilities.InputHelper;
import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
public class BookingMenu {

    public static Booking createBooking() {
        log.info("\n--- Добавление нового бронирования ---");

        BigDecimal price = InputHelper.readBigDecimal("Цена: ");
        String status = InputHelper.readNonEmptyString("Статус (подтверждено/заселен/отменено/на обработке): ");
        LocalDate checkIn = InputHelper.readLocalDate("Дата заезда (yyyy-MM-dd): ");
        String duration = InputHelper.readNonEmptyString("Длительность (HH:MM:SS): ");
        int guestId = InputHelper.readInt("ID гостя: ");

        return new Booking(price, status, checkIn, duration, guestId);
    }

    public static boolean updateBooking(Booking booking) {
        boolean updated = false;

        log.info("Текущие данные: {}", booking);
        log.info("(оставьте поле пустым, чтобы не менять)");

        BigDecimal newPrice = InputHelper.readOptionalBigDecimal(
          "Новая цена [" + booking.getPrice() + "]: ",
          booking.getPrice()
        );
        if (!newPrice.equals(booking.getPrice())) {
            booking.setPrice(newPrice);
            updated = true;
        }

        String status = InputHelper.readOptionalString("Новый статус [" + booking.getStatus() + "]: ");
        if (!status.isEmpty()) {
            booking.setStatus(status);
            updated = true;
        }

        LocalDate newCheckIn = InputHelper.readOptionalLocalDate(
          "Новая дата заезда [" + booking.getCheckInDate() + "]: ",
          booking.getCheckInDate()
        );
        if (!newCheckIn.equals(booking.getCheckInDate())) {
            booking.setCheckInDate(newCheckIn);
            updated = true;
        }

        String duration = InputHelper.readOptionalString("Новая длительность [" + booking.getDuration() + "]: ");
        if (!duration.isEmpty()) {
            booking.setDuration(duration);
            updated = true;
        }

        int newGuestId = InputHelper.readOptionalInt(
          "Новый ID гостя [" + booking.getGuestId() + "]: ",
          booking.getGuestId()
        );
        if (newGuestId != booking.getGuestId()) {
            booking.setGuestId(newGuestId);
            updated = true;
        }

        return updated;
    }
}