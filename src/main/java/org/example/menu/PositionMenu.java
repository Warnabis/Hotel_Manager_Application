package org.example.menu;

import org.example.models.Position;
import org.example.utilities.InputHelper;
import java.math.BigDecimal;

public class PositionMenu {

    public static Position createPosition() {
        System.out.println("\n--- Добавление новой должности ---");
        String title = InputHelper.readNonEmptyString("Название: ");
        BigDecimal salary = InputHelper.readBigDecimal("Зарплата: ");
        String responsibilities = InputHelper.readNonEmptyString("Обязанности: ");
        return new Position(title, salary, responsibilities);
    }

    public static boolean updatePosition(Position position) {
        boolean updated = false;
        System.out.println("Текущие данные: " + position);
        System.out.println("(оставьте поле пустым, чтобы не менять)");

        String title = InputHelper.readOptionalString("Новое название [" + position.getTitle() + "]: ");
        if (!title.isEmpty()) {
            position.setTitle(title);
            updated = true;
        }

        BigDecimal newSalary = InputHelper.readOptionalBigDecimal("Новая зарплата [" + position.getSalary() + "]: ", position.getSalary());
        if (!newSalary.equals(position.getSalary())) {
            position.setSalary(newSalary);
            updated = true;
        }

        String responsibilities = InputHelper.readOptionalString("Новые обязанности [" + position.getResponsibilities() + "]: ");
        if (!responsibilities.isEmpty()) {
            position.setResponsibilities(responsibilities);
            updated = true;
        }
        return updated;
    }
}