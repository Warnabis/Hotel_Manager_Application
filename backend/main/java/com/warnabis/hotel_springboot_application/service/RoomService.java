package com.warnabis.hotel_springboot_application.service;

import com.warnabis.hotel_springboot_application.dto.request.RoomRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.RoomResponseDto;
import com.warnabis.hotel_springboot_application.exception.ResourceNotFoundException;
import com.warnabis.hotel_springboot_application.mapper.RoomMapper;
import com.warnabis.hotel_springboot_application.model.Employee;
import com.warnabis.hotel_springboot_application.model.Room;
import com.warnabis.hotel_springboot_application.repository.EmployeeRepository;
import com.warnabis.hotel_springboot_application.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final EmployeeRepository employeeRepository;
    private final RoomMapper roomMapper;

    @Transactional(readOnly = true)
    public Page<RoomResponseDto> filterWithPagination(Integer floor, String type, String status, Double price,
                                                      Pageable pageable) {
        return roomRepository.findWithFilters(floor, type, status, price, pageable)
          .map(roomMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public RoomResponseDto findById(Integer id) {
        return roomMapper.toResponseDto(getRoomOrThrow(id));
    }

    public RoomResponseDto create(RoomRequestDto request) {
        Room room = roomMapper.toEntity(request);

        if (request.getEmployeeIds() != null && !request.getEmployeeIds().isEmpty()) {
            List<Employee> employees = employeeRepository.findAllById(
              request.getEmployeeIds().stream().map(Integer::longValue).toList()
            );
            room.getEmployees().addAll(employees);
        }
        return roomMapper.toResponseDto(roomRepository.save(room));
    }

    public RoomResponseDto update(Integer id, RoomRequestDto request) {
        Room room = getRoomOrThrow(id);
        roomMapper.updateEntity(request, room);

        if (request.getEmployeeIds() != null) {
            room.getEmployees().clear();
            if (!request.getEmployeeIds().isEmpty()) {
                List<Employee> employees = employeeRepository.findAllById(
                  request.getEmployeeIds().stream().map(Integer::longValue).toList()
                );
                room.getEmployees().addAll(employees);
            }
        }

        return roomMapper.toResponseDto(roomRepository.save(room));
    }

    public void delete(Integer id) {
        roomRepository.delete(getRoomOrThrow(id));
    }

    private Room getRoomOrThrow(Integer id) {
        return roomRepository.findById(id.longValue())
          .orElseThrow(() -> new ResourceNotFoundException("Номер", id));
    }

}