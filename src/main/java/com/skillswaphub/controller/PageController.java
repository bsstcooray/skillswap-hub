package com.skillswaphub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "SkillSwap Hub");
        model.addAttribute("content", "home-frag");
        return "layout";
    }

    @GetMapping("/ai")
    public String ai(Model model) {
        model.addAttribute("title", "Gemini Helper • SkillSwap Hub");
        model.addAttribute("content", "ai-frag");
        return "layout";
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String register() { return "register"; }
}
