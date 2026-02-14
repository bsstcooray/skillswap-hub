package com.skillswaphub.service.impl;

import com.skillswaphub.dao.AuditLogRepository;
import com.skillswaphub.dao.SkillRepository;
import com.skillswaphub.dao.UserRepository;
import com.skillswaphub.dto.SkillCreateRequestDTO;
import com.skillswaphub.dto.SkillResponseDTO;
import com.skillswaphub.exception.NotFoundException;
import com.skillswaphub.model.AuditLog;
import com.skillswaphub.model.Skill;
import com.skillswaphub.model.User;
import com.skillswaphub.service.SkillService;
import com.skillswaphub.util.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final FileStorage fileStorage;

    @Override
    public SkillResponseDTO createSkill(SkillCreateRequestDTO request, MultipartFile attachment) {
        User owner = currentUser();

        Skill skill = Skill.builder()
                .name(request.getName())
                .category(request.getCategory())
                .difficulty(request.getDifficulty())
                .owner(owner)
                .build();

        String path = fileStorage.save(attachment, "skill");
        skill.setAttachmentPath(path);

        Skill saved = skillRepository.save(skill);

        auditLogRepository.save(AuditLog.builder()
                .action("CREATE_SKILL")
                .actorUsername(owner.getUsername())
                .details("Created skill id=" + saved.getId())
                .build());

        return toDto(saved);
    }

    @Override
    public SkillResponseDTO getSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new NotFoundException("Skill not found"));
        return toDto(skill);
    }

    @Override
    public Page<SkillResponseDTO> listSkills(String category, String difficulty, String search, Pageable pageable) {
        String c = category == null ? "" : category;
        String d = difficulty == null ? "" : difficulty;
        String s = search == null ? "" : search;

        return skillRepository
                .findByDeletedFalseAndCategoryContainingIgnoreCaseAndDifficultyContainingIgnoreCaseAndNameContainingIgnoreCase(c, d, s, pageable)
                .map(this::toDto);
    }

    @Override
    public SkillResponseDTO updateSkill(Long id, SkillCreateRequestDTO request) {
        Skill skill = skillRepository.findById(id)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new NotFoundException("Skill not found"));

        User owner = currentUser();
        if (!skill.getOwner().getId().equals(owner.getId())) {
            throw new NotFoundException("Skill not found");
        }

        skill.setName(request.getName());
        skill.setCategory(request.getCategory());
        skill.setDifficulty(request.getDifficulty());

        Skill saved = skillRepository.save(skill);

        auditLogRepository.save(AuditLog.builder()
                .action("UPDATE_SKILL")
                .actorUsername(owner.getUsername())
                .details("Updated skill id=" + id)
                .build());

        return toDto(saved);
    }

    @Override
    public void softDeleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new NotFoundException("Skill not found"));

        User owner = currentUser();
        if (!skill.getOwner().getId().equals(owner.getId())) {
            throw new NotFoundException("Skill not found");
        }

        skill.setDeleted(true);
        skillRepository.save(skill);

        auditLogRepository.save(AuditLog.builder()
                .action("DELETE_SKILL")
                .actorUsername(owner.getUsername())
                .details("Soft-deleted skill id=" + id)
                .build());
    }

    private SkillResponseDTO toDto(Skill skill) {
        SkillResponseDTO dto = new SkillResponseDTO();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        dto.setCategory(skill.getCategory());
        dto.setDifficulty(skill.getDifficulty());
        dto.setOwnerUsername(skill.getOwner().getUsername());
        dto.setAttachmentUrl(skill.getAttachmentPath() == null ? null : ("/uploads/" + Paths.get(skill.getAttachmentPath()).getFileName()));
        return dto;
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Current user not found"));
    }
}
