package com.skillswaphub.service;

import com.skillswaphub.dto.GeminiRequestDTO;
import com.skillswaphub.dto.GeminiResponseDTO;

public interface GeminiService {
    GeminiResponseDTO generate(GeminiRequestDTO request);
}
