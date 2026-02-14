package com.skillswaphub.service;

import com.skillswaphub.dto.AuthRegisterRequest;
import com.skillswaphub.dto.UserResponseDTO;

public interface AuthService {
    UserResponseDTO register(AuthRegisterRequest request);
}
