package org.example.menu;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.models.Employee;
import org.example.utilities.InputHelper;

@NoArgsConstructor
@Slf4j
public class EmployeeMenu {

    public static Employee createEmployee() {
        log.info("\n--- Добавление нового сотрудника ---");
        String fullName = InputHelper.readNonEmptyString("ФИО: ");
        String phoneNumber = InputHelper.readNonEmptyString("Телефон: ");
        String experience = InputHelper.readNonEmptyString("Опыт работы: ");
        String schedule = InputHelper.readNonEmptyString("График работы: ");
        return new Employee(fullName, phoneNumber, experience, schedule);
    }

    public static boolean updateEmployee(Employee employee) {
        boolean updated = false;
        log.info("Текущие данные: {}", employee);
        log.info("(оставьте поле пустым, чтобы не менять)");

        String fullName = InputHelper.readOptionalString("Новое ФИО [" + employee.getFullName() + "]: ");
        if (!fullName.isEmpty()) {
            employee.setFullName(fullName);
            updated = true;
        }

        String phoneNumber = InputHelper.readOptionalString("Новый телефон [" + employee.getPhoneNumber() + "]: ");
        if (!phoneNumber.isEmpty()) {
            employee.setPhoneNumber(phoneNumber);
            updated = true;
        }

        String experience = InputHelper.readOptionalString("Новый опыт [" + employee.getExperience() + "]: ");
        if (!experience.isEmpty()) {
            employee.setExperience(experience);
            updated = true;
        }

        String schedule = InputHelper.readOptionalString("Новый график [" + employee.getSchedule() + "]: ");
        if (!schedule.isEmpty()) {
            employee.setSchedule(schedule);
            updated = true;
        }
        return updated;
    }
}