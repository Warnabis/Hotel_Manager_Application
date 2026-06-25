package com.warnabis.hotel_springboot_application.controller;

import com.warnabis.hotel_springboot_application.dto.request.BookingRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.BookingResponseDto;
import com.warnabis.hotel_springboot_application.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<Page<BookingResponseDto>> filterBookings(
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) Double price,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
      @RequestParam(required = false) String duration,
      @RequestParam(required = false) Integer guestId,
      @PageableDefault(size = 20, sort = "checkInDate") Pageable pageable) {
        return ResponseEntity.ok(bookingService.filterWithPagination(
          id, price, status, checkInDate, duration, guestId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> create(@Valid @RequestBody BookingRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDto> update(
      @PathVariable Long id,
      @Valid @RequestBody BookingRequestDto request) {
        return ResponseEntity.ok(bookingService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}