package com.warnabis.hotel_springboot_application.mapper;

import com.warnabis.hotel_springboot_application.dto.request.GuestRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.GuestResponseDto;
import com.warnabis.hotel_springboot_application.model.Guest;
import com.warnabis.hotel_springboot_application.model.Service;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class GuestMapper {

    public Guest toEntity(GuestRequestDto dto) {
        if (dto == null) return null;
        Guest guest = new Guest();
        guest.setFullName(dto.getFullName());
        guest.setPhoneNumber(dto.getPhoneNumber());
        guest.setEmail(dto.getEmail());
        guest.setStatus(dto.getStatus());
        return guest;
    }

    public GuestResponseDto toResponseDto(Guest guest) {
        if (guest == null) return null;
        GuestResponseDto dto = new GuestResponseDto();
        dto.setId(guest.getId());
        dto.setFullName(guest.getFullName());
        dto.setPhoneNumber(guest.getPhoneNumber());
        dto.setEmail(guest.getEmail());
        dto.setStatus(guest.getStatus());

        if (guest.getServices() != null) {
            dto.setServiceIds(guest.getServices().stream()
              .map(Service::getId)
              .collect(Collectors.toList()));
        }
        return dto;
    }

    public void updateEntity(GuestRequestDto dto, Guest guest) {
        if (dto == null || guest == null) return;
        if (dto.getFullName() != null) guest.setFullName(dto.getFullName());
        if (dto.getPhoneNumber() != null) guest.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getEmail() != null) guest.setEmail(dto.getEmail());
        if (dto.getStatus() != null) guest.setStatus(dto.getStatus());
    }
}