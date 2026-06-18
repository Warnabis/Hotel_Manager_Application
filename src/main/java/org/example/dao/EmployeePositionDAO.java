package org.example.dao;

import org.example.models.EmployeePosition;
import org.example.models.Employee;
import org.example.models.Position;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EmployeePositionDAO extends BaseRelationDAO<EmployeePosition> {

    public EmployeePositionDAO(Connection connection) {
        super(connection, "public.employee_position", "employee_id", "position_id");
    }

    @Override
    protected int getLeftId(EmployeePosition entity) {
        return entity.getEmployeeId();
    }

    @Override
    protected int getRightId(EmployeePosition entity) {
        return entity.getPositionId();
    }

    @Override
    protected EmployeePosition createInstance(int id, int leftId, int rightId) {
        return new EmployeePosition(id, leftId, rightId);
    }

    @Override
    protected void setId(EmployeePosition entity, int id) {
        entity.setId(id);
    }

    public List<Position> getPositionsByEmployeeId(int employeeId) throws SQLException {
        return getRelatedEntities(employeeId, "public.position", "position_id",
          "p.id, p.title, p.salary, p.responsibilities",
          rs -> {
              Position p = new Position();
              p.setId(rs.getInt("id"));
              p.setTitle(rs.getString("title"));
              p.setSalary(rs.getBigDecimal("salary"));
              p.setResponsibilities(rs.getString("responsibilities"));
              return p;
          });
    }

    public List<Employee> getEmployeesByPositionId(int positionId) throws SQLException {
        return getRelatedEntities(positionId, "public.employee", "employee_id",
          "e.id, e.full_name, e.phone_number, e.experience, e.schedule",
          rs -> {
              Employee e = new Employee();
              e.setId(rs.getInt("id"));
              e.setFullName(rs.getString("full_name"));
              e.setPhoneNumber(rs.getString("phone_number"));
              e.setExperience(rs.getString("experience"));
              e.setSchedule(rs.getString("schedule"));
              return e;
          });
    }
}