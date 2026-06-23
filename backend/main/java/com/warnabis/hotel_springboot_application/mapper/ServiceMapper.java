package com.warnabis.hotel_springboot_application.mapper;

import com.warnabis.hotel_springboot_application.dto.request.ServiceRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.ServiceResponseDto;
import com.warnabis.hotel_springboot_application.model.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceMapper {

    public Service toEntity(ServiceRequestDto dto) {
        if (dto == null) return null;
        Service service = new Service();
        service.setTitle(dto.getTitle());
        service.setDescription(dto.getDescription());
        service.setPrice(dto.getPrice());
        service.setDuration(dto.getDuration());
        return service;
    }

    public ServiceResponseDto toResponseDto(Service service) {
        if (service == null) return null;
        ServiceResponseDto dto = new ServiceResponseDto();
        dto.setId(service.getId());
        dto.setTitle(service.getTitle());
        dto.setDescription(service.getDescription());
        dto.setPrice(service.getPrice());
        dto.setDuration(service.getDuration());
        if (service.getGuests() != null) {
            dto.setGuestIds(service.getGuests().stream().map(g -> g.getId()).collect(Collectors.toList()));
        }
        if (service.getEmployees() != null) {
            dto.setEmployeeIds(service.getEmployees().stream().map(e -> e.getId()).collect(Collectors.toList()));
        }
        return dto;
    }

    public void updateEntity(ServiceRequestDto dto, Service service) {
        if (dto == null || service == null) return;
        if (dto.getTitle() != null) service.setTitle(dto.getTitle());
        if (dto.getDescription() != null) service.setDescription(dto.getDescription());
        if (dto.getPrice() != null) service.setPrice(dto.getPrice());
        if (dto.getDuration() != null) service.setDuration(dto.getDuration());

    }
}