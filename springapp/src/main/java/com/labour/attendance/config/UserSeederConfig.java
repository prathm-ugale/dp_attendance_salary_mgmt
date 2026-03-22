package com.labour.attendance.config;

import com.labour.attendance.entity.User;
import com.labour.attendance.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserSeederConfig {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            upsertUser(userRepository, passwordEncoder, "dhiraj", "Dhiraj@2003", "ADMIN");
            upsertUser(userRepository, passwordEncoder, "radhe", "radhe", "SUPERVISOR");
        };
    }

    private void upsertUser(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String username,
            String rawPassword,
            String role
    ) {
        User user = userRepository.findByUsername(username).orElseGet(User::new);
        user.setUsername(username);
        user.setRole(role);

        if (user.getPassword() == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }

        userRepository.save(user);
    }
}
