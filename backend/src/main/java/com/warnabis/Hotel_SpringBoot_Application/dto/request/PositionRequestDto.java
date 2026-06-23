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
public class PositionRequestDto {

    @NotBlank(message = "Название должности не может быть пустым")
    @Size(max = 255, message = "Название должности не должно превышать 255 символов")
    private String title;

    @NotNull(message = "Зарплата не может быть пустой")
    @Min(value = 0, message = "Зарплата не может быть отрицательной")
    private Double salary;

    @NotBlank(message = "Обязанности не могут быть пустыми")
    @Size(max = 1000, message = "Обязанности не должны превышать 1000 символов")
    private String responsibilities;

    private List<Integer> employeeIds;
}
