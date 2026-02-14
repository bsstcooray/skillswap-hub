package com.skillswaphub.controller;

import com.skillswaphub.dto.SkillCreateRequestDTO;
import com.skillswaphub.dto.SkillResponseDTO;
import com.skillswaphub.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ui/skills")
public class UiSkillController {

    private final SkillService skillService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "9") int size,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String difficulty,
                       @RequestParam(required = false) String search,
                       Model model) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SkillResponseDTO> result = skillService.listSkills(category, difficulty, search, pageable);

        model.addAttribute("page", result);
        model.addAttribute("category", category);
        model.addAttribute("difficulty", difficulty);
        model.addAttribute("search", search);
        return "skills";
    }

    @GetMapping("/new")
    public String createForm() {
        return "skill-new";
    }

    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam String category,
                         @RequestParam String difficulty,
                         @RequestParam(required = false) MultipartFile file,
                         RedirectAttributes ra) {
        try {
            SkillCreateRequestDTO dto = new SkillCreateRequestDTO();
            dto.setName(name);
            dto.setCategory(category);
            dto.setDifficulty(difficulty);

            SkillResponseDTO created = skillService.createSkill(dto, file);
            ra.addFlashAttribute("success", "Skill created!");
            return "redirect:/ui/skills/" + created.getId();
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/ui/skills/new";
        }
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        SkillResponseDTO skill = skillService.getSkill(id);
        model.addAttribute("skill", skill);
        return "skill-view";
    }

    @PostMapping("/{id}/delete")
    public String softDelete(@PathVariable Long id, RedirectAttributes ra) {
        skillService.softDeleteSkill(id);
        ra.addFlashAttribute("success", "Skill deleted (soft).");
        return "redirect:/ui/skills";
    }
}
