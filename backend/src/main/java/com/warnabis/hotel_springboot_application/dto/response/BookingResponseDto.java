package com.warnabis.hotel_springboot_application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {

    private Integer id;
    private Double price;
    private String status;
    private LocalDate checkInDate;
    private String duration;
    private GuestResponseDto guest;

    private List<RoomResponseDto> rooms;
    private List<PaymentResponseDto> payments;
}