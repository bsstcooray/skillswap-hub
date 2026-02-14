package com.skillswaphub.dto;

import lombok.Data;

@Data
public class SkillResponseDTO {
    private Long id;
    private String name;
    private String category;
    private String difficulty;
    private String ownerUsername;
    private String attachmentUrl;
}
