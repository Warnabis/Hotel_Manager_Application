package com.warnabis.hotel_springboot_application.controller;

import com.warnabis.hotel_springboot_application.dto.request.JoinLinkRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.JoinLinkResponseDto;
import com.warnabis.hotel_springboot_application.dto.response.JoinTableTypeResponseDto;
import com.warnabis.hotel_springboot_application.join.JoinTableType;
import com.warnabis.hotel_springboot_application.service.JoinTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/joins")
@RequiredArgsConstructor
public class JoinTableController {

    private final JoinTableService joinTableService;

    @GetMapping("/types")
    public ResponseEntity<List<JoinTableTypeResponseDto>> getTypes() {
        return ResponseEntity.ok(joinTableService.getTypes());
    }

    @GetMapping("/{type}")
    public ResponseEntity<Page<JoinLinkResponseDto>> findPage(
            @PathVariable String type,
            @RequestParam(required = false) Integer leftId,
            @RequestParam(required = false) Integer rightId,
            @PageableDefault(size = 10, sort = "leftId") Pageable pageable) {
        return ResponseEntity.ok(joinTableService.findPage(
                JoinTableType.fromKey(type),
                leftId,
                rightId,
                pageable
        ));
    }

    @PostMapping("/{type}")
    public ResponseEntity<JoinLinkResponseDto> create(
            @PathVariable String type,
            @Valid @RequestBody JoinLinkRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(joinTableService.create(JoinTableType.fromKey(type), request));
    }

    @PutMapping("/{type}")
    public ResponseEntity<JoinLinkResponseDto> update(
            @PathVariable String type,
            @RequestParam Integer oldLeftId,
            @RequestParam Integer oldRightId,
            @Valid @RequestBody JoinLinkRequestDto request) {
        return ResponseEntity.ok(joinTableService.update(
                JoinTableType.fromKey(type),
                oldLeftId,
                oldRightId,
                request
        ));
    }

    @DeleteMapping("/{type}")
    public ResponseEntity<Void> delete(
            @PathVariable String type,
            @RequestParam Integer leftId,
            @RequestParam Integer rightId) {
        joinTableService.delete(JoinTableType.fromKey(type), leftId, rightId);
        return ResponseEntity.noContent().build();
    }
}
