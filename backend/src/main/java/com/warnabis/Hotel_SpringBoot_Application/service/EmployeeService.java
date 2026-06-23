package com.warnabis.Hotel_SpringBoot_Application.service;

import com.warnabis.Hotel_SpringBoot_Application.dto.request.EmployeeRequestDto;
import com.warnabis.Hotel_SpringBoot_Application.dto.response.EmployeeResponseDto;
import com.warnabis.Hotel_SpringBoot_Application.exception.ResourceNotFoundException;
import com.warnabis.Hotel_SpringBoot_Application.mapper.EmployeeMapper;
import com.warnabis.Hotel_SpringBoot_Application.model.Employee;
import com.warnabis.Hotel_SpringBoot_Application.model.Position;
import com.warnabis.Hotel_SpringBoot_Application.model.Room;
import com.warnabis.Hotel_SpringBoot_Application.model.Service;
import com.warnabis.Hotel_SpringBoot_Application.repository.EmployeeRepository;
import com.warnabis.Hotel_SpringBoot_Application.repository.PositionRepository;
import com.warnabis.Hotel_SpringBoot_Application.repository.RoomRepository;
import com.warnabis.Hotel_SpringBoot_Application.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final RoomRepository roomRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeeMapper employeeMapper;


    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> filterWithPagination(String fullName, String experience,
                                                          String schedule, String phoneNumber,
                                                          Pageable pageable) {
        return employeeRepository.findWithFilters(fullName, experience, schedule, phoneNumber, pageable)
          .map(employeeMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto findById(Integer id) {
        return employeeMapper.toResponseDto(getEmployeeOrThrow(id));
    }

    public EmployeeResponseDto create(EmployeeRequestDto request) {
        Employee employee = employeeMapper.toEntity(request);

        if (request.getPositionIds() != null && !request.getPositionIds().isEmpty()) {
            List<Position> positions = positionRepository.findAllById(
              request.getPositionIds().stream().map(Integer::longValue).toList()
            );
            employee.getPositions().addAll(positions);
        }
        if (request.getRoomIds() != null && !request.getRoomIds().isEmpty()) {
            List<Room> rooms = roomRepository.findAllById(
              request.getRoomIds().stream().map(Integer::longValue).toList()
            );
            employee.getRooms().addAll(rooms);
        }
        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
            List<Service> services = serviceRepository.findAllById(
              request.getServiceIds().stream().map(Integer::longValue).toList()
            );
            employee.getServices().addAll(services);
        }

        return employeeMapper.toResponseDto(employeeRepository.save(employee));
    }

    public EmployeeResponseDto update(Integer id, EmployeeRequestDto request) {
        Employee employee = getEmployeeOrThrow(id);
        employeeMapper.updateEntity(request, employee);

        if (request.getPositionIds() != null) {
            employee.getPositions().clear();
            if (!request.getPositionIds().isEmpty()) {
                List<Position> positions = positionRepository.findAllById(
                  request.getPositionIds().stream().map(Integer::longValue).toList()
                );
                employee.getPositions().addAll(positions);
            }
        }
        if (request.getRoomIds() != null) {
            employee.getRooms().clear();
            if (!request.getRoomIds().isEmpty()) {
                List<Room> rooms = roomRepository.findAllById(
                  request.getRoomIds().stream().map(Integer::longValue).toList()
                );
                employee.getRooms().addAll(rooms);
            }
        }
        if (request.getServiceIds() != null) {
            employee.getServices().clear();
            if (!request.getServiceIds().isEmpty()) {
                List<Service> services = serviceRepository.findAllById(
                  request.getServiceIds().stream().map(Integer::longValue).toList()
                );
                employee.getServices().addAll(services);
            }
        }

        return employeeMapper.toResponseDto(employeeRepository.save(employee));
    }

    public void delete(Integer id) {
        employeeRepository.delete(getEmployeeOrThrow(id));
    }

    private Employee getEmployeeOrThrow(Integer id) {
        return employeeRepository.findById(id.longValue())
          .orElseThrow(() -> new ResourceNotFoundException("Сотрудник", id));
    }
}