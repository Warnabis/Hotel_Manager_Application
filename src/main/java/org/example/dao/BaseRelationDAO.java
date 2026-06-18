package org.example.dao;

import org.example.utilities.DaoUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRelationDAO<T> implements BaseDAO<T> {

    protected final Connection connection;
    protected final String tableName;
    protected final String idColumn = "id";
    protected final String leftColumn;
    protected final String rightColumn;

    protected BaseRelationDAO(Connection connection, String tableName, String leftColumn, String rightColumn) {
        this.connection = connection;
        this.tableName = tableName;
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
    }

    protected abstract int getLeftId(T entity);
    protected abstract int getRightId(T entity);
    protected abstract T createInstance(int id, int leftId, int rightId);
    protected abstract void setId(T entity, int id);

    @Override
    public void create(T entity) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (" + leftColumn + ", " + rightColumn + ") VALUES (?, ?)";
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
        String sql = "UPDATE " + tableName + " SET " + leftColumn + " = ?, " + rightColumn + " = ? WHERE " + idColumn + " = ?";
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
        String sql = "SELECT " + idColumn + ", " + leftColumn + ", " + rightColumn + " FROM " + tableName + " ORDER BY " + idColumn;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(createInstance(rs.getInt(idColumn), rs.getInt(leftColumn), rs.getInt(rightColumn)));
            }
        }
        return list;
    }

    @Override
    public T findById(int id) throws SQLException {
        String sql = "SELECT " + idColumn + ", " + leftColumn + ", " + rightColumn + " FROM " + tableName + " WHERE " + idColumn + " = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createInstance(rs.getInt(idColumn), rs.getInt(leftColumn), rs.getInt(rightColumn));
                }
            }
        }
        return null;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE " + idColumn + " = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public boolean exists(int leftId, int rightId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + leftColumn + " = ? AND " + rightColumn + " = ?";
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

    public void deleteByLeftId(int leftId) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE " + leftColumn + " = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, leftId);
            pstmt.executeUpdate();
        }
    }

    public void deleteByRightId(int rightId) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE " + rightColumn + " = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, rightId);
            pstmt.executeUpdate();
        }
    }

    protected <R> List<R> getRelatedEntities(int id, String targetTable, String joinColumn, String selectClause, RowMapper<R> mapper) throws SQLException {
        List<R> result = new ArrayList<>();
        String sql = "SELECT " + selectClause + " FROM " + targetTable + " t " +
          "JOIN " + tableName + " rel ON t." + idColumn + " = rel." + joinColumn +
          " WHERE rel." + (joinColumn.equals(leftColumn) ? rightColumn : leftColumn) + " = ?" +
          " ORDER BY t." + idColumn;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.mapRow(rs));
                }
            }
        }
        return result;
    }

    @FunctionalInterface
    public interface RowMapper<R> {
        R mapRow(ResultSet rs) throws SQLException;
    }
}