package com.skillswaphub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillCreateRequestDTO {
    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    @Size(max = 60)
    private String category;

    @NotBlank
    @Size(max = 20)
    private String difficulty;
}
