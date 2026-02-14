package com.skillswaphub.controller;

import com.skillswaphub.dto.GeminiRequestDTO;
import com.skillswaphub.dto.GeminiResponseDTO;
import com.skillswaphub.service.GeminiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class GeminiController {

    private final GeminiService geminiService;

    @PostMapping("/generate")
    public ResponseEntity<GeminiResponseDTO> getAIResponse(@Valid @RequestBody GeminiRequestDTO request) {
        return ResponseEntity.ok(geminiService.generate(request));
    }
}
