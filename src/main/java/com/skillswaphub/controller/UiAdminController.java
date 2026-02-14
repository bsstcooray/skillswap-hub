package com.skillswaphub.controller;

import com.skillswaphub.dao.ExchangeRequestRepository;
import com.skillswaphub.dao.RoleRepository;
import com.skillswaphub.dao.UserRepository;
import com.skillswaphub.exception.NotFoundException;
import com.skillswaphub.model.ExchangeRequest;
import com.skillswaphub.model.Role;
import com.skillswaphub.model.RoleName;
import com.skillswaphub.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UiAdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("title", "Admin Dashboard");
        model.addAttribute("content", "admin-frag");
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("exchangeCount", exchangeRequestRepository.count());
        model.addAttribute("recentExchanges", exchangeRequestRepository.findTop10ByOrderByCreatedAtDesc());
        return "layout";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("title", "Manage Users");
        model.addAttribute("content", "admin-users-frag");
        model.addAttribute("users", userRepository.findAll());
        return "layout";
    }

    @PostMapping("/users/{id}/promote")
    public String promoteToAdmin(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).build()));
        user.addRole(adminRole);
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/exchanges/export.csv")
    public ResponseEntity<byte[]> exportExchangesCsv() {
        List<ExchangeRequest> exchanges = exchangeRequestRepository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("id,requester,receiver,offeredSkill,requestedSkill,status,proposedTime,createdAt\n");

        DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        for (ExchangeRequest ex : exchanges) {
            sb.append(ex.getId()).append(",");
            sb.append(safe(ex.getRequester().getUsername())).append(",");
            sb.append(safe(ex.getReceiver().getUsername())).append(",");
            sb.append(safe(ex.getOfferedSkill().getName())).append(",");
            sb.append(safe(ex.getRequestedSkill().getName())).append(",");
            sb.append(ex.getStatus().name()).append(",");
            sb.append(ex.getProposedTime() == null ? "" : dtf.format(ex.getProposedTime())).append(",");
            sb.append(ex.getCreatedAt() == null ? "" : dtf.format(ex.getCreatedAt())).append("\n");
        }

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exchanges.csv")
                .contentType(new MediaType("text", "csv"))
                .body(bytes);
    }

    private String safe(String s) {
        if (s == null) return "";

        // Remove line breaks
        String cleaned = s.replace("\n", " ").replace("\r", " ");

        // Escape double quotes for CSV
        cleaned = cleaned.replace("\"", "\"\"");

        // Wrap in quotes if it contains commas or quotes
        if (cleaned.contains(",") || cleaned.contains("\"")) {
            return "\"" + cleaned + "\"";
        }

        return cleaned;
    }
}
