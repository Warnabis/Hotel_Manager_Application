package org.example.menu;

import lombok.extern.slf4j.Slf4j;
import org.example.models.Service;
import org.example.utilities.InputHelper;
import java.math.BigDecimal;

@Slf4j
public class ServiceMenu {

    private ServiceMenu() {}

    public static Service createService() {
        log.info("\n--- Добавление новой услуги ---");
        String title = InputHelper.readNonEmptyString("Название: ");
        String description = InputHelper.readNonEmptyString("Описание: ");
        BigDecimal price = InputHelper.readBigDecimal("Цена: ");
        String duration = InputHelper.readNonEmptyString("Длительность (HH:MM:SS): ");
        return new Service(title, description, price, duration);
    }

    public static boolean updateService(Service service) {
        boolean updated = false;
        log.info("Текущие данные: {}", service);
        log.info("(оставьте поле пустым, чтобы не менять)");

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