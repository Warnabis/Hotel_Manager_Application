package com.warnabis.hotel_springboot_application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDto {

    @NotBlank(message = "ФИО не может быть пустым")
    @Size(max = 255, message = "ФИО не должно превышать 255 символов")
    private String fullName;

    @NotBlank(message = "Опыт работы не может быть пустым")
    @Size(max = 255, message = "Опыт работы не должен превышать 255 символов")
    private String experience;

    @NotBlank(message = "График работы не может быть пустым")
    @Size(max = 255, message = "График работы не должен превышать 255 символов")
    private String schedule;

    @NotBlank(message = "Телефон не может быть пустым")
    @Size(max = 50, message = "Телефон не должен превышать 50 символов")
    private String phoneNumber;

    private List<Integer> positionIds;
    private List<Integer> roomIds;
    private List<Integer> serviceIds;
}
