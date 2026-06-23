package com.warnabis.Hotel_SpringBoot_Application.controller;

import com.warnabis.Hotel_SpringBoot_Application.dto.request.PaymentRequestDto;
import com.warnabis.Hotel_SpringBoot_Application.dto.response.PaymentResponseDto;
import com.warnabis.Hotel_SpringBoot_Application.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;


@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<Page<PaymentResponseDto>> filterPayments(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Double amount,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
      @RequestParam(required = false) String paymentMethod,
      @PageableDefault(size = 20, sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(paymentService.filterWithPagination(
          status, amount, paymentDate, paymentMethod, pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> create(@Valid @RequestBody PaymentRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> update(
      @PathVariable Integer id,
      @Valid @RequestBody PaymentRequestDto request) {
        return ResponseEntity.ok(paymentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}