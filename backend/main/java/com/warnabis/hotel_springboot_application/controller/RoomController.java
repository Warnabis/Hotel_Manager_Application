package com.warnabis.hotel_springboot_application.controller;

import com.warnabis.hotel_springboot_application.dto.request.RoomRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.RoomResponseDto;
import com.warnabis.hotel_springboot_application.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<Page<RoomResponseDto>> filterRooms(
      @RequestParam(required = false) Integer floor,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Double price,
      @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(roomService.filterWithPagination(
          floor, type, status, price, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(roomService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RoomResponseDto> create(@Valid @RequestBody RoomRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDto> update(
      @PathVariable Integer id,
      @Valid @RequestBody RoomRequestDto request) {
        return ResponseEntity.ok(roomService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}