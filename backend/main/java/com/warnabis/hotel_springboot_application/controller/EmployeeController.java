package com.warnabis.hotel_springboot_application.controller;

import com.warnabis.hotel_springboot_application.dto.request.EmployeeRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.EmployeeResponseDto;
import com.warnabis.hotel_springboot_application.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<Page<EmployeeResponseDto>> filterEmployees(
      @RequestParam(required = false) String fullName,
      @RequestParam(required = false) String experience,
      @RequestParam(required = false) String schedule,
      @RequestParam(required = false) String phoneNumber,
      @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return ResponseEntity.ok(employeeService.filterWithPagination(
          fullName, experience, schedule, phoneNumber, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDto> create(@Valid @RequestBody EmployeeRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> update(
      @PathVariable Integer id,
      @Valid @RequestBody EmployeeRequestDto request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}