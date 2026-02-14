package com.skillswaphub.service;

import com.skillswaphub.dto.SkillCreateRequestDTO;
import com.skillswaphub.dto.SkillResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface SkillService {
    SkillResponseDTO createSkill(SkillCreateRequestDTO request, MultipartFile attachment);
    SkillResponseDTO getSkill(Long id);
    Page<SkillResponseDTO> listSkills(String category, String difficulty, String search, Pageable pageable);
    SkillResponseDTO updateSkill(Long id, SkillCreateRequestDTO request);
    void softDeleteSkill(Long id);
}
