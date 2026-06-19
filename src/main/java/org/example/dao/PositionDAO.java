package org.example.dao;

import org.example.models.Position;
import java.sql.*;

public class PositionDAO extends BaseEntityDAO<Position> {

    public PositionDAO(Connection connection) {
        super(connection, "public.position");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.position (title, salary, responsibilities) VALUES (?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.position SET title = ?, salary = ?, responsibilities = ? WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT id, title, salary, responsibilities FROM public.position ORDER BY id";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id, title, salary, responsibilities FROM public.position WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM public.position WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Position position) throws SQLException {
        pstmt.setString(1, position.getTitle());
        pstmt.setBigDecimal(2, position.getSalary());
        pstmt.setString(3, position.getResponsibilities());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Position position) throws SQLException {
        pstmt.setString(1, position.getTitle());
        pstmt.setBigDecimal(2, position.getSalary());
        pstmt.setString(3, position.getResponsibilities());
        pstmt.setInt(4, position.getId());
    }

    @Override
    protected Position mapRow(ResultSet rs) throws SQLException {
        Position position = new Position();
        position.setId(rs.getInt("id"));
        position.setTitle(rs.getString("title"));
        position.setSalary(rs.getBigDecimal("salary"));
        position.setResponsibilities(rs.getString("responsibilities"));
        return position;
    }

    @Override
    protected void setId(Position entity, int id) {
        entity.setId(id);
    }

    @Override
    protected int getId(Position entity) {
        return entity.getId();
    }

    @Override
    public void delete(int id) throws SQLException {
        String deleteRelations = "DELETE FROM public.employee_position WHERE position_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteRelations)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        super.delete(id);
    }
}