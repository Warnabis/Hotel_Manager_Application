package org.example.dao;

import org.example.utilities.DaoUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRelationDAO<T> implements BaseDAO<T> {

    protected final Connection connection;
    protected final String tableName;

    protected BaseRelationDAO(Connection connection, String tableName) {
        this.connection = connection;
        this.tableName = DaoUtils.sanitizeTableName(tableName);
    }

    protected abstract String getInsertSql();
    protected abstract String getUpdateSql();
    protected abstract String getFindAllSql();
    protected abstract String getFindByIdSql();
    protected abstract String getDeleteSql();
    protected abstract String getExistsSql();

    protected abstract int getLeftId(T entity);
    protected abstract int getRightId(T entity);
    protected abstract T createInstance(ResultSet rs) throws SQLException;
    protected abstract void setId(T entity, int id);

    @Override
    public void create(T entity) throws SQLException {
        String sql = getInsertSql();
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, getLeftId(entity));
            pstmt.setInt(2, getRightId(entity));
            pstmt.executeUpdate();
            int id = DaoUtils.getGeneratedId(pstmt, connection, tableName);
            setId(entity, id);
        }
    }

    @Override
    public void update(T entity) throws SQLException {
        String sql = getUpdateSql();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, getLeftId(entity));
            pstmt.setInt(2, getRightId(entity));
            pstmt.setInt(3, getId(entity));
            pstmt.executeUpdate();
        }
    }

    private int getId(T entity) {
        try {
            return (int) entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить ID", e);
        }
    }

    @Override
    public List<T> findAll() throws SQLException {
        List<T> list = new ArrayList<>();
        String sql = getFindAllSql();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(createInstance(rs));
            }
        }
        return list;
    }

    @Override
    public T findById(int id) throws SQLException {
        String sql = getFindByIdSql();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createInstance(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = getDeleteSql();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public boolean exists(int leftId, int rightId) throws SQLException {
        String sql = getExistsSql();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, leftId);
            pstmt.setInt(2, rightId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}