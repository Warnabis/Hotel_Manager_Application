package com.warnabis.Hotel_SpringBoot_Application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDto {

    @NotNull(message = "Этаж не может быть пустым")
    @Min(value = 1, message = "Этаж должен быть больше 0")
    private Integer floor;

    @NotBlank(message = "Тип номера не может быть пустым")
    @Size(max = 100, message = "Тип номера не должен превышать 100 символов")
    private String type;

    @NotBlank(message = "Статус не может быть пустым")
    @Size(max = 50, message = "Статус не должен превышать 50 символов")
    private String status;

    @NotNull(message = "Цена не может быть пустой")
    @Min(value = 0, message = "Цена не может быть отрицательной")
    private Double price;

    private List<Integer> employeeIds;
    private List<Integer> bookingIds;
}
