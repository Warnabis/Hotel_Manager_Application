package com.warnabis.hotel_springboot_application.controller;

import com.warnabis.hotel_springboot_application.dto.request.PositionRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.PositionResponseDto;
import com.warnabis.hotel_springboot_application.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/position")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public ResponseEntity<Page<PositionResponseDto>> filterPositions(
      @RequestParam(required = false) String title,
      @RequestParam(required = false) Double salary,
      @RequestParam(required = false) String description,
      @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(positionService.filterWithPagination(title, salary, description, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(positionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PositionResponseDto> create(@Valid @RequestBody PositionRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(positionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PositionResponseDto> update(
      @PathVariable Integer id,
      @Valid @RequestBody PositionRequestDto request) {
        return ResponseEntity.ok(positionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        positionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}