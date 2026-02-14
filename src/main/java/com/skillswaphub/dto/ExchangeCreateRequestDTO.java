package com.skillswaphub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExchangeCreateRequestDTO {

    // API can send receiverUserId OR UI can send receiverUsername
    private Long receiverUserId;
    private String receiverUsername;

    @NotNull
    private Long offeredSkillId;

    @NotNull
    private Long requestedSkillId;

    private LocalDateTime proposedTime;
}
