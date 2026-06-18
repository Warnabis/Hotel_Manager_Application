package org.example.utilities;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.BaseDAO;
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

    public static <T> void createEntity(BaseDAO<T> dao, String entityName, Creator<T> creator) throws SQLException {
        T entity = creator.create();
        dao.create(entity);
        log.info("{} успешно добавлен! ID: {}", entityName, getEntityId(entity));
    }

    public static <T> void findAllEntities(BaseDAO<T> dao, String entityName) throws SQLException {
        log.info("\n--- Список всех {}ов ---", entityName);
        List<T> entities = dao.findAll();
        if (entities.isEmpty()) {
            log.info("{}{}", entityName, MSG_NOT_FOUND);
        } else {
            entities.forEach(entity -> log.info(entity.toString()));
            log.info("Всего: {} {}", entities.size(), entityName);
        }
    }

    public static <T> void findEntityById(BaseDAO<T> dao, String entityName) throws SQLException {
        log.info("\n--- Поиск {} по ID ---", entityName);
        int id = InputHelper.readInt("Введите ID: ");
        T entity = dao.findById(id);
        if (entity != null) {
            log.info("Найден: {}", entity);
        } else {
            log.info("{}{}{}", entityName, " с ID ", id, MSG_NOT_FOUND_SINGLE);
        }
    }

    public static <T> void updateEntity(BaseDAO<T> dao, String entityName, Updater<T> updater) throws SQLException {
        log.info("\n--- Обновление данных {} ---", entityName);
        int id = InputHelper.readInt("Введите ID " + entityName + ": ");
        T entity = dao.findById(id);
        if (entity == null) {
            log.info("{}{}", entityName, MSG_NOT_FOUND_SINGLE);
            return;
        }
        log.info("Текущие данные: {}", entity);
        log.info("(оставьте поле пустым, чтобы не менять)");
        boolean updated = updater.update(entity);
        if (updated) {
            dao.update(entity);
            log.info(MSG_UPDATED);
        } else {
            log.info(MSG_NO_CHANGES);
        }
    }

    public static <T> void deleteEntity(BaseDAO<T> dao, String entityName) throws SQLException {
        log.info("\n--- Удаление {} ---", entityName);
        int id = InputHelper.readInt("Введите ID " + entityName + ": ");
        T entity = dao.findById(id);
        if (entity == null) {
            log.info("{}{}", entityName, MSG_NOT_FOUND_SINGLE);
            return;
        }
        if (InputHelper.confirmAction("Вы уверены, что хотите удалить? " + entity)) {
            dao.delete(id);
            log.info("{}{}", entityName, MSG_DELETED);
        } else {
            log.info(MSG_DELETE_CANCELLED);
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
            log.error("Не удалось получить ID", e);
            return -1;
        }
    }
}