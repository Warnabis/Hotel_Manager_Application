package com.warnabis.hotel_springboot_application.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSchemaPatcher implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_PASSWORD}")
    private String defaultGuestPassword;

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute(
          "ALTER TABLE guest ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255)"
        );

        Integer guestsWithoutPassword = jdbcTemplate.queryForObject(
          """
            SELECT COUNT(*) FROM guest
            WHERE password_hash IS NULL OR TRIM(password_hash) = ''
            """,
          Integer.class
        );

        if (guestsWithoutPassword != null && guestsWithoutPassword > 0) {
            String encodedPassword = passwordEncoder.encode(defaultGuestPassword);
            int updated = jdbcTemplate.update(
              """
                UPDATE guest
                SET password_hash = ?
                WHERE password_hash IS NULL OR TRIM(password_hash) = ''
                """,
              encodedPassword
            );
            log.info(
              "Assigned default guest password to {} account(s). Use password: {}",
              updated,
              defaultGuestPassword
            );
        }
    }
}
