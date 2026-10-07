package com.dev.smartlabbackend.init;

import com.dev.smartlabbackend.entity.Role;
import com.dev.smartlabbackend.entity.User;
import com.dev.smartlabbackend.entity.UserStatus;
import com.dev.smartlabbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            if (!userRepository.existsByOrgEmail("admin@smartlab.vn")) {
                User admin = User.builder()
                        .orgEmail("admin@smartlab.vn")
                        .passwordHash(passwordEncoder.encode("Admin@123"))
                        .fullName("System Administrator")
                        .department("IT Administration")
                        .role(Role.IT_ADMIN)
                        .status(UserStatus.ACTIVE)
                        .phone("0987654321")
                        .dateOfBirth(LocalDate.of(1995, 5, 20))
                        .note("Default System Administrator account")
                        .avatarUrl("https://ui-avatars.com/api/?name=Admin+SmartLab&background=0D8ABC&color=fff")
                        .build();
                userRepository.save(admin);
                log.info("Default IT Admin initialized: admin@smartlab.vn / Admin@123");
            }

            if (!userRepository.existsByOrgEmail("manager@smartlab.vn")) {
                User manager = User.builder()
                        .orgEmail("manager@smartlab.vn")
                        .passwordHash(passwordEncoder.encode("Manager@123"))
                        .fullName("Lab Manager")
                        .department("SmartLab Core")
                        .role(Role.MANAGER)
                        .status(UserStatus.ACTIVE)
                        .phone("0912345678")
                        .dateOfBirth(LocalDate.of(1998, 8, 15))
                        .note("Default Manager account for project oversight")
                        .avatarUrl("https://ui-avatars.com/api/?name=Lab+Manager&background=6366F1&color=fff")
                        .build();
                userRepository.save(manager);
                log.info("Default Manager initialized: manager@smartlab.vn / Manager@123");
            }

            if (!userRepository.existsByOrgEmail("member@smartlab.vn")) {
                User member = User.builder()
                        .orgEmail("member@smartlab.vn")
                        .passwordHash(passwordEncoder.encode("Member@123"))
                        .fullName("Lab Member")
                        .department("Software Engineering")
                        .role(Role.MEMBER)
                        .status(UserStatus.ACTIVE)
                        .phone("0909090909")
                        .dateOfBirth(LocalDate.of(2002, 1, 1))
                        .note("Default Member account")
                        .avatarUrl("https://ui-avatars.com/api/?name=Lab+Member&background=10B981&color=fff")
                        .build();
                userRepository.save(member);
                log.info("Default Member initialized: member@smartlab.vn / Member@123");
            }
        } catch (Exception e) {
            log.warn("DataInitializer skipped or encountered issue: {}", e.getMessage());
        }
    }
}
