package com.warnabis.hotel_springboot_application.mapper;

import com.warnabis.hotel_springboot_application.dto.request.EmployeeRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.EmployeeResponseDto;
import com.warnabis.hotel_springboot_application.model.Employee;
import com.warnabis.hotel_springboot_application.model.Position;
import com.warnabis.hotel_springboot_application.model.Room;
import com.warnabis.hotel_springboot_application.model.Service;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequestDto dto) {
        if (dto == null) return null;
        Employee employee = new Employee();
        employee.setFullName(dto.getFullName());
        employee.setExperience(dto.getExperience());
        employee.setSchedule(dto.getSchedule());
        employee.setPhoneNumber(dto.getPhoneNumber());
        return employee;
    }

    public EmployeeResponseDto toResponseDto(Employee employee) {
        if (employee == null) return null;
        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setId(employee.getId());
        dto.setFullName(employee.getFullName());
        dto.setExperience(employee.getExperience());
        dto.setSchedule(employee.getSchedule());
        dto.setPhoneNumber(employee.getPhoneNumber());

        if (employee.getPositions() != null) {
            dto.setPositionIds(employee.getPositions().stream()
              .map(Position::getId)
              .collect(Collectors.toList()));
        }
        if (employee.getRooms() != null) {
            dto.setRoomIds(employee.getRooms().stream()
              .map(Room::getId)
              .collect(Collectors.toList()));
        }
        if (employee.getServices() != null) {
            dto.setServiceIds(employee.getServices().stream()
              .map(Service::getId)
              .collect(Collectors.toList()));
        }
        return dto;
    }

    public void updateEntity(EmployeeRequestDto dto, Employee employee) {
        if (dto == null || employee == null) return;
        if (dto.getFullName() != null) employee.setFullName(dto.getFullName());
        if (dto.getExperience() != null) employee.setExperience(dto.getExperience());
        if (dto.getSchedule() != null) employee.setSchedule(dto.getSchedule());
        if (dto.getPhoneNumber() != null) employee.setPhoneNumber(dto.getPhoneNumber());
    }
}