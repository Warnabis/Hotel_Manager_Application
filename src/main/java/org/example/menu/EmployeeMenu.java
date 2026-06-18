package org.example.menu;

import org.example.models.Employee;
import org.example.utilities.InputHelper;

public class EmployeeMenu {

    public static Employee createEmployee() {
        System.out.println("\n--- Добавление нового сотрудника ---");
        String fullName = InputHelper.readNonEmptyString("ФИО: ");
        String phoneNumber = InputHelper.readNonEmptyString("Телефон: ");
        String experience = InputHelper.readNonEmptyString("Опыт работы: ");
        String schedule = InputHelper.readNonEmptyString("График работы: ");
        return new Employee(fullName, phoneNumber, experience, schedule);
    }

    public static boolean updateEmployee(Employee employee) {
        boolean updated = false;
        System.out.println("Текущие данные: " + employee);
        System.out.println("(оставьте поле пустым, чтобы не менять)");

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