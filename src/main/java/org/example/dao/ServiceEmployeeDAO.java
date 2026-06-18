package org.example.dao;

import org.example.models.ServiceEmployee;
import org.example.models.Service;
import org.example.models.Employee;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ServiceEmployeeDAO extends BaseRelationDAO<ServiceEmployee> {

    public ServiceEmployeeDAO(Connection connection) {
        super(connection, "public.service_employee", "service_id", "employee_id");
    }

    @Override
    protected int getLeftId(ServiceEmployee entity) {
        return entity.getServiceId();
    }

    @Override
    protected int getRightId(ServiceEmployee entity) {
        return entity.getEmployeeId();
    }

    @Override
    protected ServiceEmployee createInstance(int id, int leftId, int rightId) {
        return new ServiceEmployee(id, leftId, rightId);
    }

    @Override
    protected void setId(ServiceEmployee entity, int id) {
        entity.setId(id);
    }

    public List<Employee> getEmployeesByServiceId(int serviceId) throws SQLException {
        return getRelatedEntities(serviceId, "public.employee", "employee_id",
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

    public List<Service> getServicesByEmployeeId(int employeeId) throws SQLException {
        return getRelatedEntities(employeeId, "public.service", "service_id",
          "s.id, s.title, s.description, s.price, s.duration",
          rs -> {
              Service s = new Service();
              s.setId(rs.getInt("id"));
              s.setTitle(rs.getString("title"));
              s.setDescription(rs.getString("description"));
              s.setPrice(rs.getBigDecimal("price"));
              s.setDuration(rs.getString("duration"));
              return s;
          });
    }
}