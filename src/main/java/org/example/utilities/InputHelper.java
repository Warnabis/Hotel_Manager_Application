package org.example.utilities;

import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

@Slf4j
public class InputHelper {

    private InputHelper() {}

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String readNonEmptyString(String prompt) {
        while (true) {
            log.info(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            log.warn("Ошибка: поле не может быть пустым");
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            log.info(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                log.warn("Ошибка: введите целое число");
            }
        }
    }

    public static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            log.info(prompt);
            try {
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                log.warn("Ошибка: введите корректное число");
            }
        }
    }

    public static LocalDate readLocalDate(String prompt) {
        while (true) {
            log.info(prompt);
            try {
                return LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                log.warn("Ошибка: неверный формат даты. Используйте формат yyyy-MM-dd");
            }
        }
    }

    public static String readOptionalString(String prompt) {
        log.info(prompt);
        return scanner.nextLine().trim();
    }

    public static int readOptionalInt(String prompt, int defaultValue) {
        while (true) {
            log.info(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                log.warn("Ошибка: введите целое число или оставьте пустым для пропуска");
            }
        }
    }

    public static BigDecimal readOptionalBigDecimal(String prompt, BigDecimal defaultValue) {
        while (true) {
            log.info(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                log.warn("Ошибка: введите корректное число или оставьте пустым для пропуска");
            }
        }
    }

    public static LocalDate readOptionalLocalDate(String prompt, LocalDate defaultValue) {
        while (true) {
            log.info(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                log.warn("Ошибка: неверный формат даты. Используйте yyyy-MM-dd или оставьте пустым для пропуска");
            }
        }
    }

    public static int readMenuChoice(String prompt) {
        while (true) {
            log.info(prompt);
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 0) {
                    return choice;
                }
                log.warn("Ошибка: введите неотрицательное число");
            } catch (NumberFormatException e) {
                log.warn("Ошибка: введите номер пункта меню");
            }
        }
    }

    public static boolean confirmAction(String prompt) {
        while (true) {
            log.info(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            }
            if (input.equals("n") || input.equals("no")) {
                return false;
            }
            log.warn("Ошибка: введите y или n");
        }
    }


}