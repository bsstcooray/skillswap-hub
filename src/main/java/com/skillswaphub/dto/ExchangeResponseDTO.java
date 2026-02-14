package com.skillswaphub.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExchangeResponseDTO {
    private Long id;
    private String requester;
    private String receiver;
    private Long offeredSkillId;
    private Long requestedSkillId;
    private String status;
    private LocalDateTime proposedTime;
}
