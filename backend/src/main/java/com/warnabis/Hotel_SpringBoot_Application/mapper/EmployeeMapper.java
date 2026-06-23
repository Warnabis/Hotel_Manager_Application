package com.warnabis.Hotel_SpringBoot_Application.mapper;

import com.warnabis.Hotel_SpringBoot_Application.dto.request.EmployeeRequestDto;
import com.warnabis.Hotel_SpringBoot_Application.dto.response.EmployeeResponseDto;
import com.warnabis.Hotel_SpringBoot_Application.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
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
            dto.setPositionIds(employee.getPositions().stream().map(p -> p.getId()).collect(Collectors.toList()));
        }
        if (employee.getRooms() != null) {
            dto.setRoomIds(employee.getRooms().stream().map(r -> r.getId()).collect(Collectors.toList()));
        }
        if (employee.getServices() != null) {
            dto.setServiceIds(employee.getServices().stream().map(s -> s.getId()).collect(Collectors.toList()));
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