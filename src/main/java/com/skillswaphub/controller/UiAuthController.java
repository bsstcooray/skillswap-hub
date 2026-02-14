package com.skillswaphub.controller;

import com.skillswaphub.dto.AuthRegisterRequest;
import com.skillswaphub.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UiAuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             Model model) {
        try {
            AuthRegisterRequest req = new AuthRegisterRequest();
            req.setUsername(username);
            req.setEmail(email);
            req.setPassword(password);

            authService.register(req);
            model.addAttribute("registered", true);
            return "register";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "register";
        }
    }
}
