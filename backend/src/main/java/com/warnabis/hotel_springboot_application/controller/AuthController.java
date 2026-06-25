package com.warnabis.hotel_springboot_application.controller;

import com.warnabis.hotel_springboot_application.dto.request.AdminLoginRequestDto;
import com.warnabis.hotel_springboot_application.dto.request.ChangePasswordRequestDto;
import com.warnabis.hotel_springboot_application.dto.request.LoginRequestDto;
import com.warnabis.hotel_springboot_application.dto.request.RegisterRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.AuthResponseDto;
import com.warnabis.hotel_springboot_application.dto.response.GuestResponseDto;
import com.warnabis.hotel_springboot_application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponseDto> adminLogin(@Valid @RequestBody AdminLoginRequestDto request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }

    @GetMapping("/me")
    public ResponseEntity<GuestResponseDto> me() {
        return ResponseEntity.ok(authService.getCurrentGuestProfile());
    }

    @DeleteMapping("/account")
    public ResponseEntity<Void> deleteAccount() {
        authService.deleteOwnAccount();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }
}
