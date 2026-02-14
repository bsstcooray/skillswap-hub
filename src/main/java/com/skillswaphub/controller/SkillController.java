package com.skillswaphub.controller;

import com.skillswaphub.dto.SkillCreateRequestDTO;
import com.skillswaphub.dto.SkillResponseDTO;
import com.skillswaphub.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<Page<SkillResponseDTO>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(skillService.listSkills(category, difficulty, search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkill(id));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SkillResponseDTO> create(
            @Valid @RequestPart("data") SkillCreateRequestDTO data,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createSkill(data, file));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<SkillResponseDTO> update(@PathVariable Long id, @Valid @RequestBody SkillCreateRequestDTO data) {
        return ResponseEntity.ok(skillService.updateSkill(id, data));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        skillService.softDeleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
