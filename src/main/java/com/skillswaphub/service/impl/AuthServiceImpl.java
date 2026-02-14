package com.skillswaphub.service.impl;

import com.skillswaphub.dao.ProfileRepository;
import com.skillswaphub.dao.RoleRepository;
import com.skillswaphub.dao.UserRepository;
import com.skillswaphub.dto.AuthRegisterRequest;
import com.skillswaphub.dto.UserResponseDTO;
import com.skillswaphub.exception.BadRequestException;
import com.skillswaphub.model.Profile;
import com.skillswaphub.model.Role;
import com.skillswaphub.model.RoleName;
import com.skillswaphub.model.User;
import com.skillswaphub.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO register(AuthRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build()));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        user.addRole(userRole);

        User saved = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saved)
                .bio("")
                .location("")
                .build();
        profileRepository.save(profile);

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(saved.getId());
        dto.setUsername(saved.getUsername());
        dto.setEmail(saved.getEmail());
        dto.setRoles(saved.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()));
        return dto;
    }
}
