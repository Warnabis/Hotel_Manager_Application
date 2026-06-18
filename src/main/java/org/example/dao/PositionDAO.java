package org.example.dao;

import org.example.models.Position;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PositionDAO implements BaseDAO<Position> {

    private final Connection connection;

    public PositionDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Position position) throws SQLException {
        String sql = "INSERT INTO public.position (title, salary, responsibilities) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, position.getTitle());
            pstmt.setBigDecimal(2, position.getSalary());
            pstmt.setString(3, position.getResponsibilities());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    position.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Position> findAll() throws SQLException {
        List<Position> positions = new ArrayList<>();
        String sql = "SELECT * FROM public.position ORDER BY id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Position position = new Position();
                position.setId(rs.getInt("id"));
                position.setTitle(rs.getString("title"));
                position.setSalary(rs.getBigDecimal("salary"));
                position.setResponsibilities(rs.getString("responsibilities"));
                positions.add(position);
            }
        }
        return positions;
    }

    @Override
    public Position findById(int id) throws SQLException {
        String sql = "SELECT * FROM public.position WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Position position = new Position();
                    position.setId(rs.getInt("id"));
                    position.setTitle(rs.getString("title"));
                    position.setSalary(rs.getBigDecimal("salary"));
                    position.setResponsibilities(rs.getString("responsibilities"));
                    return position;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Position position) throws SQLException {
        String sql = "UPDATE public.position SET title = ?, salary = ?, responsibilities = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, position.getTitle());
            pstmt.setBigDecimal(2, position.getSalary());
            pstmt.setString(3, position.getResponsibilities());
            pstmt.setInt(4, position.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String deleteRelations = "DELETE FROM public.employee_position WHERE position_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteRelations)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }

        String sql = "DELETE FROM public.position WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}