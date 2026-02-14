package com.skillswaphub.service.impl;

import com.skillswaphub.dao.ExchangeRequestRepository;
import com.skillswaphub.model.ExchangeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ExchangeRequestRepository exchangeRepo;
    private final JavaMailSender mailSender; // optional; configure SMTP to actually send emails

    @Scheduled(fixedDelay = 60_000)
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in60 = now.plusMinutes(60);

        exchangeRepo.findAll().stream()
                .filter(ex -> ex.getStatus() == ExchangeRequest.Status.ACCEPTED)
                .filter(ex -> ex.getProposedTime() != null)
                .filter(ex -> !ex.isDeleted())
                .filter(ex -> ex.getProposedTime().isAfter(now) && ex.getProposedTime().isBefore(in60))
                .forEach(ex -> log.info("Reminder: session soon for requestId={} at {}", ex.getId(), ex.getProposedTime()));
    }
}
