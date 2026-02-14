package com.skillswaphub.controller;

import com.skillswaphub.dto.ExchangeCreateRequestDTO;
import com.skillswaphub.dto.ExchangeResponseDTO;
import com.skillswaphub.service.ExchangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class ExchangeController {

    private final ExchangeService exchangeService;

    @PostMapping
    public ResponseEntity<ExchangeResponseDTO> create(@Valid @RequestBody ExchangeCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exchangeService.create(request));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<ExchangeResponseDTO> accept(@PathVariable Long id) {
        return ResponseEntity.ok(exchangeService.accept(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ExchangeResponseDTO> complete(@PathVariable Long id) {
        return ResponseEntity.ok(exchangeService.complete(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ExchangeResponseDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(exchangeService.cancel(id));
    }

    @GetMapping("/incoming")
    public ResponseEntity<Page<ExchangeResponseDTO>> incoming(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(exchangeService.incoming(pageable));
    }

    @GetMapping("/outgoing")
    public ResponseEntity<Page<ExchangeResponseDTO>> outgoing(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(exchangeService.outgoing(pageable));
    }
}
