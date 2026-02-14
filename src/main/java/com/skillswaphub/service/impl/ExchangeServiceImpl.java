package com.skillswaphub.service.impl;

import com.skillswaphub.dao.AuditLogRepository;
import com.skillswaphub.dao.ExchangeRequestRepository;
import com.skillswaphub.dao.SkillRepository;
import com.skillswaphub.dao.UserRepository;
import com.skillswaphub.dto.ExchangeCreateRequestDTO;
import com.skillswaphub.dto.ExchangeResponseDTO;
import com.skillswaphub.exception.BadRequestException;
import com.skillswaphub.exception.NotFoundException;
import com.skillswaphub.model.AuditLog;
import com.skillswaphub.model.ExchangeRequest;
import com.skillswaphub.model.Skill;
import com.skillswaphub.model.User;
import com.skillswaphub.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeRequestRepository exchangeRepo;
    private final UserRepository userRepo;
    private final SkillRepository skillRepo;
    private final AuditLogRepository auditRepo;

    @Override
    public ExchangeResponseDTO create(ExchangeCreateRequestDTO request) {
        User requester = currentUser();

        User receiver;
if (request.getReceiverUserId() != null) {
    receiver = userRepo.findById(request.getReceiverUserId())
            .orElseThrow(() -> new NotFoundException("Receiver not found"));
} else if (request.getReceiverUsername() != null && !request.getReceiverUsername().isBlank()) {
    receiver = userRepo.findByUsername(request.getReceiverUsername())
            .orElseThrow(() -> new NotFoundException("Receiver not found"));
} else {
    throw new BadRequestException("Receiver is required");
}


        Skill offered = skillRepo.findById(request.getOfferedSkillId())
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new NotFoundException("Offered skill not found"));

        Skill requested = skillRepo.findById(request.getRequestedSkillId())
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new NotFoundException("Requested skill not found"));

        if (!offered.getOwner().getId().equals(requester.getId())) {
            throw new BadRequestException("You can only offer your own skill");
        }
        if (!requested.getOwner().getId().equals(receiver.getId())) {
            throw new BadRequestException("Requested skill must belong to receiver");
        }

        ExchangeRequest ex = ExchangeRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .offeredSkill(offered)
                .requestedSkill(requested)
                .proposedTime(request.getProposedTime())
                .build();

        ExchangeRequest saved = exchangeRepo.save(ex);

        auditRepo.save(AuditLog.builder()
                .action("CREATE_REQUEST")
                .actorUsername(requester.getUsername())
                .details("requestId=" + saved.getId())
                .build());

        return toDto(saved);
    }

    @Override
    public ExchangeResponseDTO accept(Long id) {
        ExchangeRequest ex = exchangeRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        User me = currentUser();
        if (!ex.getReceiver().getId().equals(me.getId())) {
            throw new NotFoundException("Request not found");
        }
        if (ex.getStatus() != ExchangeRequest.Status.PENDING) {
            throw new BadRequestException("Only PENDING requests can be accepted");
        }

        ex.setStatus(ExchangeRequest.Status.ACCEPTED);
        ExchangeRequest saved = exchangeRepo.save(ex);

        auditRepo.save(AuditLog.builder()
                .action("ACCEPT_REQUEST")
                .actorUsername(me.getUsername())
                .details("requestId=" + id)
                .build());

        return toDto(saved);
    }

    @Override
    public ExchangeResponseDTO complete(Long id) {
        ExchangeRequest ex = exchangeRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        User me = currentUser();
        if (!ex.getRequester().getId().equals(me.getId()) && !ex.getReceiver().getId().equals(me.getId())) {
            throw new NotFoundException("Request not found");
        }
        if (ex.getStatus() != ExchangeRequest.Status.ACCEPTED) {
            throw new BadRequestException("Only ACCEPTED requests can be completed");
        }

        ex.setStatus(ExchangeRequest.Status.COMPLETED);
        ExchangeRequest saved = exchangeRepo.save(ex);

        auditRepo.save(AuditLog.builder()
                .action("COMPLETE_REQUEST")
                .actorUsername(me.getUsername())
                .details("requestId=" + id)
                .build());

        return toDto(saved);
    }

    @Override
    public ExchangeResponseDTO cancel(Long id) {
        ExchangeRequest ex = exchangeRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        User me = currentUser();
        if (!ex.getRequester().getId().equals(me.getId()) && !ex.getReceiver().getId().equals(me.getId())) {
            throw new NotFoundException("Request not found");
        }
        if (ex.getStatus() == ExchangeRequest.Status.COMPLETED) {
            throw new BadRequestException("Completed requests cannot be cancelled");
        }

        ex.setStatus(ExchangeRequest.Status.CANCELLED);
        ExchangeRequest saved = exchangeRepo.save(ex);

        auditRepo.save(AuditLog.builder()
                .action("CANCEL_REQUEST")
                .actorUsername(me.getUsername())
                .details("requestId=" + id)
                .build());

        return toDto(saved);
    }

    @Override
    public Page<ExchangeResponseDTO> incoming(Pageable pageable) {
        return exchangeRepo.findByReceiver(currentUser(), pageable).map(this::toDto);
    }

    @Override
    public Page<ExchangeResponseDTO> outgoing(Pageable pageable) {
        return exchangeRepo.findByRequester(currentUser(), pageable).map(this::toDto);
    }

    private ExchangeResponseDTO toDto(ExchangeRequest ex) {
        ExchangeResponseDTO dto = new ExchangeResponseDTO();
        dto.setId(ex.getId());
        dto.setRequester(ex.getRequester().getUsername());
        dto.setReceiver(ex.getReceiver().getUsername());
        dto.setOfferedSkillId(ex.getOfferedSkill().getId());
        dto.setRequestedSkillId(ex.getRequestedSkill().getId());
        dto.setStatus(ex.getStatus().name());
        dto.setProposedTime(ex.getProposedTime());
        return dto;
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Current user not found"));
    }
}
