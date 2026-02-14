package com.skillswaphub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GeminiRequestDTO {
    @NotBlank(message = "Prompt cannot be empty")
    private String prompt;
}
