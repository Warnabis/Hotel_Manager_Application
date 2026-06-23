package com.warnabis.Hotel_SpringBoot_Application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestRequestDto {

    @NotBlank(message = "ФИО не может быть пустым")
    @Size(max = 255, message = "ФИО не должно превышать 255 символов")
    private String fullName;

    @NotBlank(message = "Телефон не может быть пустым")
    @Size(max = 50, message = "Телефон не должен превышать 50 символов")
    private String phoneNumber;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    @Size(max = 255, message = "Email не должен превышать 255 символов")
    private String email;

    @NotBlank(message = "Статус не может быть пустым")
    @Size(max = 50, message = "Статус не должен превышать 50 символов")
    private String status;

    private List<Integer> serviceIds;
}
