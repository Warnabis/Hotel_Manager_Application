package com.warnabis.Hotel_SpringBoot_Application.service;

import com.warnabis.Hotel_SpringBoot_Application.dto.request.PaymentRequestDto;
import com.warnabis.Hotel_SpringBoot_Application.dto.response.PaymentResponseDto;
import com.warnabis.Hotel_SpringBoot_Application.exception.ResourceNotFoundException;
import com.warnabis.Hotel_SpringBoot_Application.mapper.PaymentMapper;
import com.warnabis.Hotel_SpringBoot_Application.model.Payment;
import com.warnabis.Hotel_SpringBoot_Application.model.Booking;
import com.warnabis.Hotel_SpringBoot_Application.model.Service;
import com.warnabis.Hotel_SpringBoot_Application.repository.PaymentRepository;
import com.warnabis.Hotel_SpringBoot_Application.repository.BookingRepository;
import com.warnabis.Hotel_SpringBoot_Application.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final PaymentMapper paymentMapper;

    @Transactional(readOnly = true)
    public Page<PaymentResponseDto> filterWithPagination(String status, Double amount,
                                                         LocalDate paymentDate, String paymentMethod,
                                                         Pageable pageable) {
        return paymentRepository.findWithFilters(status, amount, paymentDate, paymentMethod, pageable)
          .map(paymentMapper::toResponseDto);
    }


    @Transactional(readOnly = true)
    public PaymentResponseDto findById(Integer id) {
        return paymentMapper.toResponseDto(getPaymentOrThrow(id));
    }

    public PaymentResponseDto create(PaymentRequestDto request) {
        Payment payment = paymentMapper.toEntity(request);

        if (request.getBookingIds() != null && !request.getBookingIds().isEmpty()) {
            List<Booking> bookings = bookingRepository.findAllById(
              request.getBookingIds().stream()
                .map(Integer::longValue)
                .collect(Collectors.toList())
            );
            payment.getBookings().addAll(bookings);
        }
        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
            List<Service> services = serviceRepository.findAllById(
              request.getServiceIds().stream()
                .map(Integer::longValue)
                .collect(Collectors.toList())
            );
            payment.getServices().addAll(services);
        }

        return paymentMapper.toResponseDto(paymentRepository.save(payment));
    }

    public PaymentResponseDto update(Integer id, PaymentRequestDto request) {
        Payment payment = getPaymentOrThrow(id);
        paymentMapper.updateEntity(request, payment);

        if (request.getBookingIds() != null) {
            payment.getBookings().clear();
            if (!request.getBookingIds().isEmpty()) {
                List<Booking> bookings = bookingRepository.findAllById(
                  request.getBookingIds().stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toList())
                );
                payment.getBookings().addAll(bookings);
            }
        }
        if (request.getServiceIds() != null) {
            payment.getServices().clear();
            if (!request.getServiceIds().isEmpty()) {
                List<Service> services = serviceRepository.findAllById(
                  request.getServiceIds().stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toList())
                );
                payment.getServices().addAll(services);
            }
        }

        return paymentMapper.toResponseDto(paymentRepository.save(payment));
    }

    public void delete(Integer id) {
        paymentRepository.delete(getPaymentOrThrow(id));
    }

    private Payment getPaymentOrThrow(Integer id) {
        return paymentRepository.findById(id.longValue())
          .orElseThrow(() -> new ResourceNotFoundException("Платеж", id));
    }
}