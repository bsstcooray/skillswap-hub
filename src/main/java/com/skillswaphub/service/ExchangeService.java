package com.skillswaphub.service;

import com.skillswaphub.dto.ExchangeCreateRequestDTO;
import com.skillswaphub.dto.ExchangeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExchangeService {
    ExchangeResponseDTO create(ExchangeCreateRequestDTO request);
    ExchangeResponseDTO accept(Long id);
    ExchangeResponseDTO complete(Long id);
    ExchangeResponseDTO cancel(Long id);
    Page<ExchangeResponseDTO> incoming(Pageable pageable);
    Page<ExchangeResponseDTO> outgoing(Pageable pageable);
}
