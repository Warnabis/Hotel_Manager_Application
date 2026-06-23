package com.warnabis.Hotel_SpringBoot_Application.mapper;

import com.warnabis.Hotel_SpringBoot_Application.dto.request.PositionRequestDto;
import com.warnabis.Hotel_SpringBoot_Application.dto.response.PositionResponseDto;
import com.warnabis.Hotel_SpringBoot_Application.model.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PositionMapper {

    public Position toEntity(PositionRequestDto dto) {
        if (dto == null) return null;
        Position position = new Position();
        position.setTitle(dto.getTitle());
        position.setSalary(dto.getSalary());
        position.setResponsibilities(dto.getResponsibilities());
        return position;
    }

    public PositionResponseDto toResponseDto(Position position) {
        if (position == null) return null;
        PositionResponseDto dto = new PositionResponseDto();
        dto.setId(position.getId());
        dto.setTitle(position.getTitle());
        dto.setSalary(position.getSalary());
        dto.setResponsibilities(position.getResponsibilities());
        if (position.getEmployees() != null) {
            dto.setEmployeeIds(position.getEmployees().stream().map(e -> e.getId()).collect(Collectors.toList()));
        }
        return dto;
    }

    public void updateEntity(PositionRequestDto dto, Position position) {
        if (dto == null || position == null) return;
        if (dto.getTitle() != null) position.setTitle(dto.getTitle());
        if (dto.getSalary() != null) position.setSalary(dto.getSalary());
        if (dto.getResponsibilities() != null) position.setResponsibilities(dto.getResponsibilities());

    }
}