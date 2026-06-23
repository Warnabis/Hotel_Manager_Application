package com.warnabis.Hotel_SpringBoot_Application.service;

import com.warnabis.Hotel_SpringBoot_Application.dto.request.PositionRequestDto;
import com.warnabis.Hotel_SpringBoot_Application.dto.response.PositionResponseDto;
import com.warnabis.Hotel_SpringBoot_Application.exception.ResourceNotFoundException;
import com.warnabis.Hotel_SpringBoot_Application.mapper.PositionMapper;
import com.warnabis.Hotel_SpringBoot_Application.model.Position;
import com.warnabis.Hotel_SpringBoot_Application.model.Employee;
import com.warnabis.Hotel_SpringBoot_Application.repository.PositionRepository;
import com.warnabis.Hotel_SpringBoot_Application.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionService {

    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionMapper positionMapper;

    @Transactional(readOnly = true)
    public PositionResponseDto findById(Integer id) {
        return positionMapper.toResponseDto(getPositionOrThrow(id));
    }

    public PositionResponseDto create(PositionRequestDto request) {
        Position position = positionMapper.toEntity(request);

        if (request.getEmployeeIds() != null && !request.getEmployeeIds().isEmpty()) {
            List<Employee> employees = employeeRepository.findAllById(
              request.getEmployeeIds().stream().map(Integer::longValue).toList()
            );
            position.getEmployees().addAll(employees);
        }

        return positionMapper.toResponseDto(positionRepository.save(position));
    }

    public PositionResponseDto update(Integer id, PositionRequestDto request) {
        Position position = getPositionOrThrow(id);
        positionMapper.updateEntity(request, position);

        if (request.getEmployeeIds() != null) {
            position.getEmployees().clear();
            if (!request.getEmployeeIds().isEmpty()) {
                List<Employee> employees = employeeRepository.findAllById(
                  request.getEmployeeIds().stream().map(Integer::longValue).toList()
                );
                position.getEmployees().addAll(employees);
            }
        }

        return positionMapper.toResponseDto(positionRepository.save(position));
    }

    public void delete(Integer id) {
        positionRepository.delete(getPositionOrThrow(id));
    }

    private Position getPositionOrThrow(Integer id) {
        return positionRepository.findById(id.longValue())
          .orElseThrow(() -> new ResourceNotFoundException("Должность", id));
    }

    @Transactional(readOnly = true)
    public Page<PositionResponseDto> filterWithPagination(String title, Double salary, String description,
                                                          Pageable pageable) {
        return positionRepository.findWithFilters(title, salary, description, pageable)
          .map(positionMapper::toResponseDto);
    }
}