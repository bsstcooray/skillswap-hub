package com.skillswaphub.config;

import com.skillswaphub.dao.RoleRepository;
import com.skillswaphub.dao.UserRepository;
import com.skillswaphub.model.Role;
import com.skillswaphub.model.RoleName;
import com.skillswaphub.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build()));

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).build()));

        userRepository.findByUsername("admin").orElseGet(() -> {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@skillswap.local")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .build();
            admin.addRole(userRole);
            admin.addRole(adminRole);
            return userRepository.save(admin);
        });
    }
}
