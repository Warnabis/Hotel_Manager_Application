package com.warnabis.hotel_springboot_application.controller;

import com.warnabis.hotel_springboot_application.dto.request.ServiceRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.ServiceResponseDto;
import com.warnabis.hotel_springboot_application.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<Page<ServiceResponseDto>> filterServices(
      @RequestParam(required = false) String title,
      @RequestParam(required = false) Double price,
      @RequestParam(required = false) String duration,
      @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(serviceService.filterWithPagination(
          title, price, duration, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(serviceService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDto> create(@Valid @RequestBody ServiceRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> update(
      @PathVariable Integer id,
      @Valid @RequestBody ServiceRequestDto request) {
        return ResponseEntity.ok(serviceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}