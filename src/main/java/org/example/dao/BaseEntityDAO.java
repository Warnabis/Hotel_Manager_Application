package org.example.dao;

import org.example.utilities.DaoUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseEntityDAO<T> implements BaseDAO<T> {

    protected final Connection connection;
    protected final String tableName;
    protected final static String idColumn = "id";

    protected BaseEntityDAO(Connection connection, String tableName) {
        this.connection = connection;
        this.tableName = tableName;
    }

    protected abstract String getInsertSql();
    protected abstract void setInsertParameters(PreparedStatement pstmt, T entity) throws SQLException;
    protected abstract T mapRow(ResultSet rs) throws SQLException;
    protected abstract void setId(T entity, int id);
    protected abstract int getId(T entity);
    protected abstract String getUpdateSql();
    protected abstract void setUpdateParameters(PreparedStatement pstmt, T entity) throws SQLException;

    @Override
    public void create(T entity) throws SQLException {
        String sql = getInsertSql();
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setInsertParameters(pstmt, entity);
            pstmt.executeUpdate();
            int id = DaoUtils.getGeneratedId(pstmt, connection, tableName);
            setId(entity, id);
        }
    }

    @Override
    public List<T> findAll() throws SQLException {
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " ORDER BY " + idColumn;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public T findById(int id) throws SQLException {
        String sql = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void update(T entity) throws SQLException {
        String sql = getUpdateSql();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setUpdateParameters(pstmt, entity);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE " + idColumn + " = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}