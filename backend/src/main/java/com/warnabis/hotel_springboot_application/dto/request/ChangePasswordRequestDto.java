package com.warnabis.hotel_springboot_application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    @NotBlank(message = "Новый пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String newPassword;
}