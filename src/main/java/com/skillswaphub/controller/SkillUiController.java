package com.skillswaphub.controller;

import com.skillswaphub.dao.SkillRepository;
import com.skillswaphub.dao.UserRepository;
import com.skillswaphub.dto.SkillCreateRequestDTO;
import com.skillswaphub.dto.SkillResponseDTO;
import com.skillswaphub.model.Skill;
import com.skillswaphub.model.User;
import com.skillswaphub.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SkillUiController {

    private final SkillService skillService;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @GetMapping("/skills")
    public String skills(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "9") int size,
                         @RequestParam(required = false) String category,
                         @RequestParam(required = false) String difficulty,
                         @RequestParam(required = false) String search,
                         Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SkillResponseDTO> skills = skillService.listSkills(category, difficulty, search, pageable);

        model.addAttribute("title", "Browse Skills • SkillSwap Hub");
        model.addAttribute("content", "skills-frag");
        model.addAttribute("page", skills);
        model.addAttribute("category", category);
        model.addAttribute("difficulty", difficulty);
        model.addAttribute("search", search);
        return "layout";
    }

    @GetMapping("/skills/new")
    public String newSkill(Model model) {
        model.addAttribute("title", "Offer a Skill • SkillSwap Hub");
        model.addAttribute("content", "skill-form-frag");
        model.addAttribute("mode", "new");
        model.addAttribute("form", new SkillCreateRequestDTO());
        return "layout";
    }

    @PostMapping("/skills/new")
    public String createSkill(@RequestParam String name,
                              @RequestParam String category,
                              @RequestParam String difficulty,
                              @RequestParam(required = false) MultipartFile file,
                              Model model) {
        SkillCreateRequestDTO dto = new SkillCreateRequestDTO();
        dto.setName(name);
        dto.setCategory(category);
        dto.setDifficulty(difficulty);

        SkillResponseDTO created = skillService.createSkill(dto, file);
        return "redirect:/skills/" + created.getId();
    }

    @GetMapping("/skills/{id}")
    public String skillDetail(@PathVariable Long id, Model model) {
        SkillResponseDTO skill = skillService.getSkill(id);

        boolean isOwner = false;
        List<SkillResponseDTO> mySkills = List.of();

        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getName())) {

            String me = SecurityContextHolder.getContext().getAuthentication().getName();
            isOwner = me.equals(skill.getOwnerUsername());

            // My skills for the exchange dropdown (simple repo query, mapped to DTO fields used by UI)
            User user = userRepository.findByUsername(me).orElse(null);
            if (user != null) {
                mySkills = skillRepository.findByOwnerAndDeletedFalse(user).stream().map(this::toDto).toList();
            }
        }

        model.addAttribute("title", skill.getName() + " • SkillSwap Hub");
        model.addAttribute("content", "skill-detail-frag");
        model.addAttribute("skill", skill);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("mySkills", mySkills);
        return "layout";
    }

    @GetMapping("/skills/{id}/edit")
    public String editSkill(@PathVariable Long id, Model model) {
        SkillResponseDTO skill = skillService.getSkill(id);

        SkillCreateRequestDTO form = new SkillCreateRequestDTO();
        form.setName(skill.getName());
        form.setCategory(skill.getCategory());
        form.setDifficulty(skill.getDifficulty());

        model.addAttribute("title", "Edit Skill • SkillSwap Hub");
        model.addAttribute("content", "skill-form-frag");
        model.addAttribute("mode", "edit");
        model.addAttribute("skillId", id);
        model.addAttribute("form", form);
        return "layout";
    }

    @PostMapping("/skills/{id}/edit")
    public String doEdit(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam String category,
                         @RequestParam String difficulty) {
        SkillCreateRequestDTO dto = new SkillCreateRequestDTO();
        dto.setName(name);
        dto.setCategory(category);
        dto.setDifficulty(difficulty);
        skillService.updateSkill(id, dto);
        return "redirect:/skills/" + id;
    }

    @PostMapping("/skills/{id}/delete")
    public String delete(@PathVariable Long id) {
        skillService.softDeleteSkill(id);
        return "redirect:/skills";
    }

    private SkillResponseDTO toDto(Skill s) {
        SkillResponseDTO dto = new SkillResponseDTO();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setCategory(s.getCategory());
        dto.setDifficulty(s.getDifficulty());
        dto.setOwnerUsername(s.getOwner().getUsername());
        dto.setAttachmentUrl(s.getAttachmentPath() == null ? null : "/uploads/" + s.getAttachmentPath());
        return dto;
    }
}
