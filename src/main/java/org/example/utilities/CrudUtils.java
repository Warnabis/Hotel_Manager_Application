package org.example.utilities;

import org.example.dao.BaseDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class CrudUtils {
    private static final Scanner scanner = new Scanner(System.in);

    public static <T> void createEntity(
      BaseDAO<T> dao,
      String entityName,
      Creator<T> creator) throws SQLException {

        T entity = creator.create();
        dao.create(entity);
        System.out.println(entityName + " успешно добавлен(а)! ID: " + getEntityId(entity));
    }

    public static <T> void findAllEntities(
      BaseDAO<T> dao,
      String entityName) throws SQLException {

        System.out.println("\n--- Список всех " + entityName + "ов ---");
        List<T> entities = dao.findAll();

        if (entities.isEmpty()) {
            System.out.println(entityName + " не найдены.");
        } else {
            entities.forEach(System.out::println);
            System.out.println("Всего: " + entities.size() + " " + entityName + "(ов)");
        }
    }

    public static <T> void findEntityById(
      BaseDAO<T> dao,
      String entityName) throws SQLException {

        System.out.println("\n--- Поиск " + entityName + " по ID ---");
        System.out.print("Введите ID: ");
        int id = InputHelper.readInt(scanner.nextLine());

        T entity = dao.findById(id);
        if (entity != null) {
            System.out.println("Найден(а): " + entity);
        } else {
            System.out.println(entityName + " с ID " + id + " не найден(а).");
        }
    }


    public static <T> void updateEntity(
      BaseDAO<T> dao,
      String entityName,
      Updater<T> updater) throws SQLException {

        System.out.println("\n--- Обновление данных " + entityName + " ---");
        System.out.print("Введите ID " + entityName + ": ");
        int id = InputHelper.readInt(scanner.nextLine());

        T entity = dao.findById(id);
        if (entity == null) {
            System.out.println(entityName + " не найден(а).");
            return;
        }

        System.out.println("Текущие данные: " + entity);
        System.out.println("(оставьте поле пустым, чтобы не менять)");

        boolean updated = updater.update(entity);
        if (updated) {
            dao.update(entity);
            System.out.println("Данные обновлены!");
        } else {
            System.out.println("Изменений не было.");
        }
    }


    public static <T> void deleteEntity(
      BaseDAO<T> dao,
      String entityName) throws SQLException {

        System.out.println("\n--- Удаление " + entityName + " ---");
        System.out.print("Введите ID " + entityName + ": ");
        int id = InputHelper.readInt(scanner.nextLine());

        T entity = dao.findById(id);
        if (entity == null) {
            System.out.println(entityName + " не найден(а).");
            return;
        }

        System.out.println("Вы уверены, что хотите удалить?");
        System.out.print(entity + " (y/n): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("y")) {
            dao.delete(id);
            System.out.println(entityName + " удален(а)!");
        } else {
            System.out.println("Удаление отменено.");
        }
    }


    @FunctionalInterface
    public interface Creator<T> {
        T create() throws SQLException;
    }

    @FunctionalInterface
    public interface Updater<T> {
        boolean update(T entity) throws SQLException;
    }

    private static <T> int getEntityId(T entity) {
        try {
            return (int) entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            return -1;
        }
    }
}