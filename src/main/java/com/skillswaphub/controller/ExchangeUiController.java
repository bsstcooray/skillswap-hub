package com.skillswaphub.controller;

import com.skillswaphub.dao.SkillRepository;
import com.skillswaphub.dao.UserRepository;
import com.skillswaphub.dto.ExchangeCreateRequestDTO;
import com.skillswaphub.dto.ExchangeResponseDTO;
import com.skillswaphub.exception.NotFoundException;
import com.skillswaphub.model.Skill;
import com.skillswaphub.model.User;
import com.skillswaphub.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ExchangeUiController {

    private final ExchangeService exchangeService;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @GetMapping("/exchanges")
    public String exchanges(@RequestParam(defaultValue = "incoming") String tab,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ExchangeResponseDTO> data = "outgoing".equalsIgnoreCase(tab)
                ? exchangeService.outgoing(pageable)
                : exchangeService.incoming(pageable);

        model.addAttribute("title", "My Exchanges • SkillSwap Hub");
        model.addAttribute("content", "exchanges-frag");
        model.addAttribute("tab", "outgoing".equalsIgnoreCase(tab) ? "outgoing" : "incoming");
        model.addAttribute("page", data);
        return "layout";
    }

    @PostMapping("/exchanges/create")
    public String create(@RequestParam Long offeredSkillId,
                         @RequestParam Long requestedSkillId,
                         @RequestParam String receiverUsername,
                         @RequestParam(required = false) String proposedTime) {

        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new NotFoundException("Receiver not found"));

        Skill requested = skillRepository.findById(requestedSkillId)
                .orElseThrow(() -> new NotFoundException("Requested skill not found"));

        ExchangeCreateRequestDTO dto = new ExchangeCreateRequestDTO();
        dto.setReceiverUserId(receiver.getId());
        dto.setOfferedSkillId(offeredSkillId);
        dto.setRequestedSkillId(requested.getId());

        if (proposedTime != null && !proposedTime.isBlank()) {
            dto.setProposedTime(LocalDateTime.parse(proposedTime));
        }

        ExchangeResponseDTO created = exchangeService.create(dto);
        return "redirect:/exchanges?tab=outgoing";
    }

    @PostMapping("/exchanges/{id}/accept")
    public String accept(@PathVariable Long id) {
        exchangeService.accept(id);
        return "redirect:/exchanges?tab=incoming";
    }

    @PostMapping("/exchanges/{id}/complete")
    public String complete(@PathVariable Long id) {
        exchangeService.complete(id);
        return "redirect:/exchanges?tab=incoming";
    }

    @PostMapping("/exchanges/{id}/cancel")
    public String cancel(@PathVariable Long id) {
        exchangeService.cancel(id);
        return "redirect:/exchanges?tab=incoming";
    }
}
