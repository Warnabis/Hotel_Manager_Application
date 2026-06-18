package org.example.menu;

import org.example.models.Service;
import org.example.utilities.InputHelper;
import java.math.BigDecimal;

public class ServiceMenu {

    public static Service createService() {
        System.out.println("\n--- Добавление новой услуги ---");
        String title = InputHelper.readNonEmptyString("Название: ");
        String description = InputHelper.readNonEmptyString("Описание: ");
        BigDecimal price = InputHelper.readBigDecimal("Цена: ");
        String duration = InputHelper.readNonEmptyString("Длительность (HH:MM:SS): ");
        return new Service(title, description, price, duration);
    }

    public static boolean updateService(Service service) {
        boolean updated = false;
        System.out.println("Текущие данные: " + service);
        System.out.println("(оставьте поле пустым, чтобы не менять)");

        String title = InputHelper.readOptionalString("Новое название [" + service.getTitle() + "]: ");
        if (!title.isEmpty()) {
            service.setTitle(title);
            updated = true;
        }

        String description = InputHelper.readOptionalString("Новое описание [" + service.getDescription() + "]: ");
        if (!description.isEmpty()) {
            service.setDescription(description);
            updated = true;
        }

        BigDecimal newPrice = InputHelper.readOptionalBigDecimal("Новая цена [" + service.getPrice() + "]: ", service.getPrice());
        if (!newPrice.equals(service.getPrice())) {
            service.setPrice(newPrice);
            updated = true;
        }

        String duration = InputHelper.readOptionalString("Новая длительность [" + service.getDuration() + "]: ");
        if (!duration.isEmpty()) {
            service.setDuration(duration);
            updated = true;
        }
        return updated;
    }
}