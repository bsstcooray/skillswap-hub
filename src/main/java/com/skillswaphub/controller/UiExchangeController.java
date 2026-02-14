package com.skillswaphub.controller;

import com.skillswaphub.dto.ExchangeCreateRequestDTO;
import com.skillswaphub.dto.ExchangeResponseDTO;
import com.skillswaphub.dto.SkillResponseDTO;
import com.skillswaphub.service.ExchangeService;
import com.skillswaphub.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ui/exchanges")
public class UiExchangeController {

    private final ExchangeService exchangeService;
    private final SkillService skillService;

    @GetMapping
    public String dashboard(@RequestParam(defaultValue = "incoming") String tab,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ExchangeResponseDTO> result = "outgoing".equalsIgnoreCase(tab)
                ? exchangeService.outgoing(pageable)
                : exchangeService.incoming(pageable);

        model.addAttribute("tab", "outgoing".equalsIgnoreCase(tab) ? "outgoing" : "incoming");
        model.addAttribute("page", result);
        return "exchanges";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Long requestedSkillId, Model model) {
        // show a simple dropdown of latest skills (top 200)
        List<SkillResponseDTO> skills = skillService
                .listSkills(null, null, null, PageRequest.of(0, 200, Sort.by("createdAt").descending()))
                .getContent();

        model.addAttribute("skills", skills);
        model.addAttribute("requestedSkillId", requestedSkillId);
        return "exchange-new";
    }

    @PostMapping
    public String create(@RequestParam String receiverUsername,
                         @RequestParam Long offeredSkillId,
                         @RequestParam Long requestedSkillId,
                         @RequestParam(required = false) String proposedTime,
                         RedirectAttributes ra) {
        try {
            ExchangeCreateRequestDTO dto = new ExchangeCreateRequestDTO();
            dto.setReceiverUsername(receiverUsername);
            dto.setOfferedSkillId(offeredSkillId);
            dto.setRequestedSkillId(requestedSkillId);

            if (proposedTime != null && !proposedTime.isBlank()) {
                try {
                    dto.setProposedTime(LocalDateTime.parse(proposedTime.trim()));
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid proposedTime format. Use yyyy-MM-ddTHH:mm (example: 2026-02-05T18:00)");
                }
            }

            exchangeService.create(dto);
            ra.addFlashAttribute("success", "Exchange request sent!");
            return "redirect:/ui/exchanges?tab=outgoing";
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/ui/exchanges/new";
        }
    }

    @PostMapping("/{id}/accept")
    public String accept(@PathVariable Long id, RedirectAttributes ra) {
        exchangeService.accept(id);
        ra.addFlashAttribute("success", "Request accepted.");
        return "redirect:/ui/exchanges?tab=incoming";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id, RedirectAttributes ra) {
        exchangeService.complete(id);
        ra.addFlashAttribute("success", "Marked as completed.");
        return "redirect:/ui/exchanges?tab=incoming";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes ra) {
        exchangeService.cancel(id);
        ra.addFlashAttribute("success", "Request cancelled.");
        return "redirect:/ui/exchanges?tab=incoming";
    }
}
