package org.example.menu;

import lombok.extern.slf4j.Slf4j;
import org.example.models.Room;
import org.example.utilities.InputHelper;
import java.math.BigDecimal;

@Slf4j
public class RoomMenu {

    private RoomMenu() {}

    public static Room createRoom() {
        log.info("\n--- Добавление нового номера ---");
        int floor = InputHelper.readInt("Этаж: ");
        String status = InputHelper.readNonEmptyString("Статус: ");
        String type = InputHelper.readNonEmptyString("Тип: ");
        BigDecimal price = InputHelper.readBigDecimal("Цена: ");
        return new Room(floor, status, type, price);
    }

    public static boolean updateRoom(Room room) {
        boolean updated = false;
        log.info("Текущие данные: {}", room);
        log.info("(оставьте поле пустым, чтобы не менять)");

        int newFloor = InputHelper.readOptionalInt("Новый этаж [" + room.getFloor() + "]: ", room.getFloor());
        if (newFloor != room.getFloor()) {
            room.setFloor(newFloor);
            updated = true;
        }

        String status = InputHelper.readOptionalString("Новый статус [" + room.getStatus() + "]: ");
        if (!status.isEmpty()) {
            room.setStatus(status);
            updated = true;
        }

        String type = InputHelper.readOptionalString("Новый тип [" + room.getType() + "]: ");
        if (!type.isEmpty()) {
            room.setType(type);
            updated = true;
        }

        BigDecimal newPrice = InputHelper.readOptionalBigDecimal("Новая цена [" + room.getPrice() + "]: ", room.getPrice());
        if (!newPrice.equals(room.getPrice())) {
            room.setPrice(newPrice);
            updated = true;
        }
        return updated;
    }
}