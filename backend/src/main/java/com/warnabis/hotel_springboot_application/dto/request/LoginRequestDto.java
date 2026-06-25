package com.warnabis.hotel_springboot_application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    private String password;
}
