package org.example.dao;

import org.example.models.RoomEmployee;
import org.example.models.Room;
import org.example.models.Employee;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RoomEmployeeDAO extends BaseRelationDAO<RoomEmployee> {

    public RoomEmployeeDAO(Connection connection) {
        super(connection, "public.room_employee", "room_id", "employee_id");
    }

    @Override
    protected int getLeftId(RoomEmployee entity) {
        return entity.getRoomId();
    }

    @Override
    protected int getRightId(RoomEmployee entity) {
        return entity.getEmployeeId();
    }

    @Override
    protected RoomEmployee createInstance(int id, int leftId, int rightId) {
        return new RoomEmployee(id, leftId, rightId);
    }

    @Override
    protected void setId(RoomEmployee entity, int id) {
        entity.setId(id);
    }

    public List<Employee> getEmployeesByRoomId(int roomId) throws SQLException {
        return getRelatedEntities(roomId, "public.employee", "employee_id",
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

    public List<Room> getRoomsByEmployeeId(int employeeId) throws SQLException {
        return getRelatedEntities(employeeId, "public.room", "room_id",
          "r.id, r.floor, r.status, r.type, r.price",
          rs -> {
              Room r = new Room();
              r.setId(rs.getInt("id"));
              r.setFloor(rs.getInt("floor"));
              r.setStatus(rs.getString("status"));
              r.setType(rs.getString("type"));
              r.setPrice(rs.getBigDecimal("price"));
              return r;
          });
    }
}