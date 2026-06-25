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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.disable())
          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login", "/auth/admin/login").permitAll()
            .requestMatchers(HttpMethod.GET, "/auth/me").hasRole("GUEST")
            .requestMatchers(HttpMethod.DELETE, "/auth/account").hasRole("GUEST")
            .requestMatchers(HttpMethod.POST, "/auth/change-password").hasRole("GUEST")
            .requestMatchers(HttpMethod.GET, "/room", "/room/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/service", "/service/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/guest/*").hasAnyRole("GUEST", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/guest/*").hasAnyRole("GUEST", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/guest/*").hasAnyRole("GUEST", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/guest").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/guest").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/booking").hasAnyRole("GUEST", "ADMIN")
            .requestMatchers(HttpMethod.GET, "/booking", "/booking/**").hasAnyRole("GUEST", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/booking/**").hasAnyRole("GUEST", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/booking/**").hasAnyRole("GUEST", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/payment").hasAnyRole("GUEST", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/payment/*").hasAnyRole("GUEST", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/payment/*").hasAnyRole("GUEST", "ADMIN")
            .anyRequest().hasRole("ADMIN")
          )
          .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Требуется авторизация\"}");
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