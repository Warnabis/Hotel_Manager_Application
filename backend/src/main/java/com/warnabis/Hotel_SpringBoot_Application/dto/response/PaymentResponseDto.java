package com.warnabis.Hotel_SpringBoot_Application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {

    private Integer id;
    private String status;
    private Double amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private List<Integer> bookingIds;
    private List<Integer> serviceIds;
}
