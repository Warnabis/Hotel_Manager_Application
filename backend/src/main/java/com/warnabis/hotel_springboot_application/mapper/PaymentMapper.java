package com.warnabis.hotel_springboot_application.mapper;

import com.warnabis.hotel_springboot_application.dto.request.PaymentRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.PaymentResponseDto;
import com.warnabis.hotel_springboot_application.model.Booking;
import com.warnabis.hotel_springboot_application.model.Payment;
import com.warnabis.hotel_springboot_application.model.Service;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequestDto dto) {
        if (dto == null) return null;
        Payment payment = new Payment();
        payment.setStatus(dto.getStatus());
        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMethod(dto.getPaymentMethod());
        return payment;
    }

    public PaymentResponseDto toResponseDto(Payment payment) {
        if (payment == null) return null;
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(payment.getId());
        dto.setStatus(payment.getStatus());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());

        if (payment.getBookings() != null) {
            dto.setBookingIds(payment.getBookings().stream()
              .map(Booking::getId)
              .toList());
        }
        if (payment.getServices() != null) {
            dto.setServiceIds(payment.getServices().stream()
              .map(Service::getId)
              .toList());
        }
        return dto;
    }

    public List<PaymentResponseDto> toResponseDtoList(List<Payment> payments) {
        if (payments == null) return List.of();
        return payments.stream()
          .map(this::toResponseDto)
          .toList();
    }

    public void updateEntity(PaymentRequestDto dto, Payment payment) {
        if (dto == null || payment == null) return;
        if (dto.getStatus() != null) payment.setStatus(dto.getStatus());
        if (dto.getAmount() != null) payment.setAmount(dto.getAmount());
        if (dto.getPaymentDate() != null) payment.setPaymentDate(dto.getPaymentDate());
        if (dto.getPaymentMethod() != null) payment.setPaymentMethod(dto.getPaymentMethod());
    }
}