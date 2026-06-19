package org.example.dao;

import org.example.models.Service;
import java.sql.*;

public class ServiceDAO extends BaseEntityDAO<Service> {

    public ServiceDAO(Connection connection) {
        super(connection, "public.service");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO public.service (title, description, price, duration) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE public.service SET title = ?, description = ?, price = ?, duration = ? WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT id, title, description, price, duration FROM public.service ORDER BY id";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id, title, description, price, duration FROM public.service WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM public.service WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Service service) throws SQLException {
        pstmt.setString(1, service.getTitle());
        pstmt.setString(2, service.getDescription());
        pstmt.setBigDecimal(3, service.getPrice());
        pstmt.setString(4, service.getDuration());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Service service) throws SQLException {
        pstmt.setString(1, service.getTitle());
        pstmt.setString(2, service.getDescription());
        pstmt.setBigDecimal(3, service.getPrice());
        pstmt.setString(4, service.getDuration());
        pstmt.setInt(5, service.getId());
    }

    @Override
    protected Service mapRow(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getInt("id"));
        service.setTitle(rs.getString("title"));
        service.setDescription(rs.getString("description"));
        service.setPrice(rs.getBigDecimal("price"));
        service.setDuration(rs.getString("duration"));
        return service;
    }

    @Override
    protected void setId(Service entity, int id) {
        entity.setId(id);
    }

    @Override
    protected int getId(Service entity) {
        return entity.getId();
    }

    @Override
    public void delete(int id) throws SQLException {
        String[] deleteQueries = {
          "DELETE FROM public.service_employee WHERE service_id = ?",
          "DELETE FROM public.service_guest WHERE service_id = ?",
          "DELETE FROM public.service_payment WHERE service_id = ?"
        };

        for (String query : deleteQueries) {
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        }
        super.delete(id);
    }
}