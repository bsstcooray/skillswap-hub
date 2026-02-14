package com.skillswaphub.service.impl;

import com.skillswaphub.dto.GeminiRequestDTO;
import com.skillswaphub.dto.GeminiResponseDTO;
import com.skillswaphub.exception.BadRequestException;
import com.skillswaphub.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {

    private final ChatModel chatModel;

    @Override
    public GeminiResponseDTO generate(GeminiRequestDTO request) {
        if (request == null || request.getPrompt() == null || request.getPrompt().isBlank()) {
            throw new BadRequestException("Prompt cannot be empty");
        }

        ChatResponse response = chatModel.call(
                new Prompt(
                        request.getPrompt(),
                        GoogleGenAiChatOptions.builder()
                                .temperature(0.4)
                                .build()
                )
        );

        String text = response.getResult().getOutput().getText();

        GeminiResponseDTO dto = new GeminiResponseDTO();
        dto.setAiResponse(text);
        return dto;
    }
}
