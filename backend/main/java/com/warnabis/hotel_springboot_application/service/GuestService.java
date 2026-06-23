package com.warnabis.hotel_springboot_application.service;

import com.warnabis.hotel_springboot_application.dto.request.GuestRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.GuestResponseDto;
import com.warnabis.hotel_springboot_application.exception.ResourceNotFoundException;
import com.warnabis.hotel_springboot_application.mapper.GuestMapper;
import com.warnabis.hotel_springboot_application.model.Guest;
import com.warnabis.hotel_springboot_application.model.Service;
import com.warnabis.hotel_springboot_application.repository.GuestRepository;
import com.warnabis.hotel_springboot_application.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class GuestService {

    private final GuestRepository guestRepository;
    private final ServiceRepository serviceRepository;
    private final GuestMapper guestMapper;


    @Transactional(readOnly = true)
    public Page<GuestResponseDto> filterWithPagination(String fullName, String phoneNumber,
                                                       String email, String status,
                                                       Pageable pageable) {
        return guestRepository.findWithFilters(fullName, phoneNumber, email, status, pageable)
          .map(guestMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public GuestResponseDto findById(Integer id) {
        return guestMapper.toResponseDto(getGuestOrThrow(id));
    }

    public GuestResponseDto create(GuestRequestDto request) {
        Guest guest = guestMapper.toEntity(request);
        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
            List<Service> services = serviceRepository.findAllById(
              request.getServiceIds().stream().map(Integer::longValue).toList()
            );
            guest.getServices().addAll(services);
        }
        return guestMapper.toResponseDto(guestRepository.save(guest));
    }

    public GuestResponseDto update(Integer id, GuestRequestDto request) {
        Guest guest = getGuestOrThrow(id);
        guestMapper.updateEntity(request, guest);

        if (request.getServiceIds() != null) {
            guest.getServices().clear();
            if (!request.getServiceIds().isEmpty()) {
                List<Service> services = serviceRepository.findAllById(
                  request.getServiceIds().stream().map(Integer::longValue).toList()
                );
                guest.getServices().addAll(services);
            }
        }
        return guestMapper.toResponseDto(guestRepository.save(guest));
    }

    public void delete(Integer id) {
        guestRepository.delete(getGuestOrThrow(id));
    }

    private Guest getGuestOrThrow(Integer id) {
        return guestRepository.findById(id.longValue())
          .orElseThrow(() -> new ResourceNotFoundException("Гость", id));
    }
}