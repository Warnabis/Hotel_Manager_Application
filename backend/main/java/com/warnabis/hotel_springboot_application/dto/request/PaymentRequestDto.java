package com.warnabis.hotel_springboot_application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    @NotBlank(message = "Статус не может быть пустым")
    @Size(max = 50, message = "Статус не должен превышать 50 символов")
    private String status;

    @NotNull(message = "Сумма не может быть пустой")
    @Min(value = 0, message = "Сумма не может быть отрицательной")
    private Double amount;

    @NotNull(message = "Дата платежа не может быть пустой")
    private LocalDate paymentDate;

    @NotBlank(message = "Способ оплаты не может быть пустым")
    @Size(max = 50, message = "Способ оплаты не должен превышать 50 символов")
    private String paymentMethod;

    private List<Integer> bookingIds;
    private List<Integer> serviceIds;
}
