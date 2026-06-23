package com.warnabis.hotel_springboot_application.dto.request;

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
public class ServiceRequestDto {

    @NotBlank(message = "Название услуги не может быть пустым")
    @Size(max = 255, message = "Название услуги не должно превышать 255 символов")
    private String title;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    @NotNull(message = "Цена не может быть пустой")
    @Min(value = 0, message = "Цена не может быть отрицательной")
    private Double price;

    @NotBlank(message = "Длительность не может быть пустой")
    @Size(max = 50, message = "Длительность не должна превышать 50 символов")
    private String duration;

    private List<Integer> guestIds;
    private List<Integer> employeeIds;
}
