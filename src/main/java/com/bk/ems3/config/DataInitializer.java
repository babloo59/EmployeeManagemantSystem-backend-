package com.bk.ems3.config;

import com.bk.ems3.model.*;
import com.bk.ems3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String adminEmail = "admin@ems.com";

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            System.out.println("✅ Admin already exists");
            return;
        }

        User admin = new User();
        admin.setFullName("System Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setFirstLogin(false);

        userRepository.save(admin);

        System.out.println("🔥 Default Admin created:");
        System.out.println("Email: admin@ems.com");
        System.out.println("Password: Admin@123");
    }
}
