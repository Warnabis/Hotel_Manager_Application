package com.warnabis.hotel_springboot_application.service;

import com.warnabis.hotel_springboot_application.dto.request.AdminLoginRequestDto;
import com.warnabis.hotel_springboot_application.dto.request.ChangePasswordRequestDto;
import com.warnabis.hotel_springboot_application.dto.request.LoginRequestDto;
import com.warnabis.hotel_springboot_application.dto.request.RegisterRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.AuthResponseDto;
import com.warnabis.hotel_springboot_application.dto.response.GuestResponseDto;
import com.warnabis.hotel_springboot_application.exception.BadRequestException;
import com.warnabis.hotel_springboot_application.exception.ConflictException;
import com.warnabis.hotel_springboot_application.mapper.GuestMapper;
import com.warnabis.hotel_springboot_application.model.Guest;
import com.warnabis.hotel_springboot_application.repository.GuestRepository;
import com.warnabis.hotel_springboot_application.security.JwtService;
import com.warnabis.hotel_springboot_application.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final GuestRepository guestRepository;
    private final GuestMapper guestMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.admin.password}")
    private String adminPassword;

    public AuthResponseDto register(RegisterRequestDto request) {
        if (guestRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new ConflictException("Гость с таким email уже зарегистрирован");
        }

        Guest guest = new Guest();
        guest.setFullName(request.getFullName().trim());
        guest.setPhoneNumber(request.getPhoneNumber().trim());
        guest.setEmail(request.getEmail().trim().toLowerCase());
        guest.setStatus("active");
        guest.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        Guest saved = guestRepository.save(guest);
        String token = jwtService.generateToken(saved.getId(), saved.getEmail(), "GUEST");
        return new AuthResponseDto(token, "GUEST", guestMapper.toResponseDto(saved));
    }

    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto request) {
        Guest guest = guestRepository.findByEmailIgnoreCase(request.getEmail().trim())
          .orElseThrow(() -> new BadRequestException("Неверный email или пароль"));

        if (guest.getPasswordHash() == null || guest.getPasswordHash().isEmpty()) {
            throw new BadRequestException("Аккаунт не имеет пароля. Обратитесь к администратору для восстановления доступа");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()
          || !passwordEncoder.matches(request.getPassword(), guest.getPasswordHash())) {
            throw new BadRequestException("Неверный email или пароль");
        }

        String token = jwtService.generateToken(guest.getId(), guest.getEmail(), "GUEST");
        return new AuthResponseDto(token, "GUEST", guestMapper.toResponseDto(guest));
    }

    public AuthResponseDto adminLogin(AdminLoginRequestDto request) {
        if (!adminPassword.equals(request.getPassword())) {
            throw new BadRequestException("Неверный пароль администратора");
        }
        String token = jwtService.generateToken(0, "admin@hotel.local", "ADMIN");
        return new AuthResponseDto(token, "ADMIN", null);
    }

    public void deleteOwnAccount() {
        UserPrincipal principal = getCurrentGuest();
        guestRepository.deleteById(principal.getUserId().longValue());
    }

    public GuestResponseDto getCurrentGuestProfile() {
        UserPrincipal principal = getCurrentGuest();
        Guest guest = guestRepository.findById(principal.getUserId().longValue())
          .orElseThrow(() -> new BadRequestException("Гость не найден"));
        return guestMapper.toResponseDto(guest);
    }

    private UserPrincipal getCurrentGuest() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BadRequestException("Требуется авторизация");
        }
        if (principal.isAdmin()) {
            throw new BadRequestException("Доступно только для гостей");
        }
        return principal;
    }

    public void changePassword(ChangePasswordRequestDto request) {
        UserPrincipal principal = getCurrentGuest();
        Guest guest = guestRepository.findById(principal.getUserId().longValue())
          .orElseThrow(() -> new BadRequestException("Гость не найден"));

        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new BadRequestException("Новый пароль обязателен");
        }

        if (request.getNewPassword().length() < 6) {
            throw new BadRequestException("Пароль должен содержать минимум 6 символов");
        }

        guest.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        guestRepository.save(guest);
    }
}