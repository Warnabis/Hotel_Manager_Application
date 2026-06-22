package org.example.utilities;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.BaseDAO;
import org.example.models.EntityWithId;

import java.sql.SQLException;
import java.util.List;

@Slf4j
public class CrudUtils {

    private static final String MSG_NOT_FOUND = " не найдены.";
    private static final String MSG_NOT_FOUND_SINGLE = " не найден.";
    private static final String MSG_NO_CHANGES = "Изменений не было.";
    private static final String MSG_UPDATED = "Данные обновлены!";
    private static final String MSG_DELETED = " удален!";
    private static final String MSG_DELETE_CANCELLED = "Удаление отменено.";

    public static <T extends EntityWithId> void createEntity(
      BaseDAO<T> dao,
      String entityName,
      Creator<T> creator) throws SQLException {

        T entity = creator.create();
        dao.create(entity);
        log.info("{} успешно добавлен! ID: {}", entityName, entity.getId());
    }

    public static <T extends EntityWithId> void findAllEntities(
      BaseDAO<T> dao,
      String entityName) throws SQLException {

        log.info("\n--- Список всех {}ов ---", entityName);
        List<T> entities = dao.findAll();
        if (entities.isEmpty()) {
            log.info("{}{}", entityName, MSG_NOT_FOUND);
        } else {
            entities.forEach(entity -> log.info("[ID: {}] {}", entity.getId(), entity));
            log.info("Всего: {} {}", entities.size(), entityName);
        }
    }

    public static <T extends EntityWithId> void findEntityById(
      BaseDAO<T> dao,
      String entityName) throws SQLException {

        log.info("\n--- Поиск {} по ID ---", entityName);
        int id = InputHelper.readInt("Введите ID: ");
        T entity = dao.findById(id);
        if (entity != null) {
            log.info("[ID: {}] {}", entity.getId(), entity);
        } else {
            log.info("{}{}", entityName, MSG_NOT_FOUND_SINGLE);
        }
    }

    public static <T extends EntityWithId> void updateEntity(
      BaseDAO<T> dao,
      String entityName,
      Updater<T> updater) throws SQLException {

        log.info("\n--- Обновление данных {} ---", entityName);
        int id = InputHelper.readInt("Введите ID " + entityName + ": ");
        T entity = dao.findById(id);
        if (entity == null) {
            log.info("{}{}", entityName, MSG_NOT_FOUND_SINGLE);
            return;
        }
        boolean updated = updater.update(entity);
        if (updated) {
            dao.update(entity);
            log.info(MSG_UPDATED);
        } else {
            log.info(MSG_NO_CHANGES);
        }
    }

    public static <T extends EntityWithId> void deleteEntity(
      BaseDAO<T> dao,
      String entityName) throws SQLException {

        log.info("\n--- Удаление {} ---", entityName);
        int id = InputHelper.readInt("Введите ID " + entityName + ": ");
        T entity = dao.findById(id);
        if (entity == null) {
            log.info("{}{}", entityName, MSG_NOT_FOUND_SINGLE);
            return;
        }
        if (InputHelper.confirmAction("Удалить [ID: " + entity.getId() + "] " + entity)) {
            dao.delete(id);
            log.info("{}{}", entityName, MSG_DELETED);
        } else {
            log.info(MSG_DELETE_CANCELLED);
        }
    }

    @FunctionalInterface
    public interface Creator<T extends EntityWithId> {
        T create() throws SQLException;
    }

    @FunctionalInterface
    public interface Updater<T extends EntityWithId> {
        boolean update(T entity) throws SQLException;
    }
}