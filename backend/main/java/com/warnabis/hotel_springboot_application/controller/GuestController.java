package com.warnabis.hotel_springboot_application.controller;

import com.warnabis.hotel_springboot_application.dto.request.GuestRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.GuestResponseDto;
import com.warnabis.hotel_springboot_application.service.GuestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @GetMapping
    public ResponseEntity<Page<GuestResponseDto>> filterGuests(
      @RequestParam(required = false) String fullName,
      @RequestParam(required = false) String phoneNumber,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String status,
      @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return ResponseEntity.ok(guestService.filterWithPagination(
          fullName, phoneNumber, email, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(guestService.findById(id));
    }

    @PostMapping
    public ResponseEntity<GuestResponseDto> create(@Valid @RequestBody GuestRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuestResponseDto> update(
      @PathVariable Integer id,
      @Valid @RequestBody GuestRequestDto request) {
        return ResponseEntity.ok(guestService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        guestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}