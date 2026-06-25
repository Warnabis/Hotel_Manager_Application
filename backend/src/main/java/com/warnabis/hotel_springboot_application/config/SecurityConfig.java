package com.warnabis.hotel_springboot_application.config;

import com.warnabis.hotel_springboot_application.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String ROLE_GUEST = "GUEST";
    private static final String ROLE_ADMIN = "ADMIN";
    
    private static final String AUTH_PATH = "/auth";
    private static final String ROOM_PATH = "/room";
    private static final String SERVICE_PATH = "/service";
    private static final String GUEST_PATH = "/guest";
    private static final String BOOKING_PATH = "/booking";
    private static final String PAYMENT_PATH = "/payment";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.disable())
          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, AUTH_PATH + "/register", AUTH_PATH + "/login", AUTH_PATH + "/admin/login").permitAll()
            .requestMatchers(HttpMethod.GET, AUTH_PATH + "/me").hasRole(ROLE_GUEST)
            .requestMatchers(HttpMethod.DELETE, AUTH_PATH + "/account").hasRole(ROLE_GUEST)
            .requestMatchers(HttpMethod.POST, AUTH_PATH + "/change-password").hasRole(ROLE_GUEST)
            .requestMatchers(HttpMethod.GET, ROOM_PATH, ROOM_PATH + "/**").permitAll()
            .requestMatchers(HttpMethod.GET, SERVICE_PATH, SERVICE_PATH + "/**").permitAll()
            .requestMatchers(HttpMethod.GET, GUEST_PATH + "/*").hasAnyRole(ROLE_GUEST, ROLE_ADMIN)
            .requestMatchers(HttpMethod.PUT, GUEST_PATH + "/*").hasAnyRole(ROLE_GUEST, ROLE_ADMIN)
            .requestMatchers(HttpMethod.DELETE, GUEST_PATH + "/*").hasAnyRole(ROLE_GUEST, ROLE_ADMIN)
            .requestMatchers(HttpMethod.POST, GUEST_PATH).hasRole(ROLE_ADMIN)
            .requestMatchers(HttpMethod.GET, GUEST_PATH).hasRole(ROLE_ADMIN)
            .requestMatchers(HttpMethod.POST, BOOKING_PATH).hasAnyRole(ROLE_GUEST, ROLE_ADMIN)
            .requestMatchers(HttpMethod.GET, BOOKING_PATH, BOOKING_PATH + "/**").hasAnyRole(ROLE_GUEST, ROLE_ADMIN)
            .requestMatchers(HttpMethod.PUT, BOOKING_PATH + "/**").hasAnyRole(ROLE_GUEST, ROLE_ADMIN)
            .requestMatchers(HttpMethod.DELETE, BOOKING_PATH + "/**").hasAnyRole(ROLE_GUEST, ROLE_ADMIN)
            .requestMatchers(HttpMethod.POST, PAYMENT_PATH).hasAnyRole(ROLE_GUEST, ROLE_ADMIN)
            .requestMatchers(HttpMethod.PUT, PAYMENT_PATH + "/*").hasAnyRole(ROLE_GUEST, ROLE_ADMIN)
            .requestMatchers(HttpMethod.DELETE, PAYMENT_PATH + "/*").hasAnyRole(ROLE_GUEST, ROLE_ADMIN)
            .anyRequest().hasRole(ROLE_ADMIN)
          )
          .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                try {
                    response.getWriter().write("{\"message\":\"Требуется авторизация\"}");
                } catch (IOException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            })
          )
          .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
