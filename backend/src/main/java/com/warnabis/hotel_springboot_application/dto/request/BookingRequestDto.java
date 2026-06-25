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
public class BookingRequestDto {

    @NotNull(message = "Цена не может быть пустой")
    @Min(value = 1, message = "Цена должна быть больше 0")
    private Double price;

    @NotNull(message = "Статус не может быть пустым")
    private String status;

    @NotNull(message = "Дата заезда не может быть пустой")
    private LocalDate checkInDate;

    @NotBlank(message = "Длительность проживания не может быть пустой")
    @Size(max = 50, message = "Длительность не должна превышать 50 символов")
    private String duration;

    private Integer guestId;

    private List<Integer> roomIds;
    private List<Integer> paymentIds;
}
