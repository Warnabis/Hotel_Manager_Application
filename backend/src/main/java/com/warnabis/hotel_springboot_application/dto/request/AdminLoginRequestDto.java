package com.warnabis.hotel_springboot_application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginRequestDto {
    @NotBlank(message = "Пароль обязателен")
    private String password;
}
