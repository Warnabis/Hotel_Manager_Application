package com.warnabis.hotel_springboot_application.service;

import com.warnabis.hotel_springboot_application.dto.request.ServiceRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.ServiceResponseDto;
import com.warnabis.hotel_springboot_application.exception.ResourceNotFoundException;
import com.warnabis.hotel_springboot_application.mapper.ServiceMapper;
import com.warnabis.hotel_springboot_application.model.Service;
import com.warnabis.hotel_springboot_application.model.Guest;
import com.warnabis.hotel_springboot_application.model.Employee;
import com.warnabis.hotel_springboot_application.repository.ServiceRepository;
import com.warnabis.hotel_springboot_application.repository.GuestRepository;
import com.warnabis.hotel_springboot_application.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final GuestRepository guestRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceMapper serviceMapper;

    @Transactional(readOnly = true)
    public Page<ServiceResponseDto> filterWithPagination(String title, Double price, String duration,
                                                         Pageable pageable) {
        return serviceRepository.findWithFilters(title, price, duration, pageable)
          .map(serviceMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public ServiceResponseDto findById(Integer id) {
        return serviceMapper.toResponseDto(getServiceOrThrow(id));
    }

    public ServiceResponseDto create(ServiceRequestDto request) {
        Service service = serviceMapper.toEntity(request);

        if (request.getGuestIds() != null && !request.getGuestIds().isEmpty()) {
            List<Guest> guests = guestRepository.findAllById(
              request.getGuestIds().stream().map(Integer::longValue).toList()
            );
            service.getGuests().addAll(guests);
        }
        if (request.getEmployeeIds() != null && !request.getEmployeeIds().isEmpty()) {
            List<Employee> employees = employeeRepository.findAllById(
              request.getEmployeeIds().stream().map(Integer::longValue).toList()
            );
            service.getEmployees().addAll(employees);
        }

        return serviceMapper.toResponseDto(serviceRepository.save(service));
    }

    public ServiceResponseDto update(Integer id, ServiceRequestDto request) {
        Service service = getServiceOrThrow(id);
        serviceMapper.updateEntity(request, service);

        if (request.getGuestIds() != null) {
            service.getGuests().clear();
            if (!request.getGuestIds().isEmpty()) {
                List<Guest> guests = guestRepository.findAllById(
                  request.getGuestIds().stream().map(Integer::longValue).toList()
                );
                service.getGuests().addAll(guests);
            }
        }
        if (request.getEmployeeIds() != null) {
            service.getEmployees().clear();
            if (!request.getEmployeeIds().isEmpty()) {
                List<Employee> employees = employeeRepository.findAllById(
                  request.getEmployeeIds().stream().map(Integer::longValue).toList()
                );
                service.getEmployees().addAll(employees);
            }
        }

        return serviceMapper.toResponseDto(serviceRepository.save(service));
    }

    public void delete(Integer id) {
        serviceRepository.delete(getServiceOrThrow(id));
    }

    private Service getServiceOrThrow(Integer id) {
        return serviceRepository.findById(id.longValue())
          .orElseThrow(() -> new ResourceNotFoundException("Услуга", id));
    }
}