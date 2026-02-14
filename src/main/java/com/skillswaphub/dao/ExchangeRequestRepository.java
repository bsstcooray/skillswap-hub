package com.skillswaphub.dao;

import com.skillswaphub.model.ExchangeRequest;
import com.skillswaphub.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {
    Page<ExchangeRequest> findByReceiver(User receiver, Pageable pageable);
    Page<ExchangeRequest> findByRequester(User requester, Pageable pageable);
    List<ExchangeRequest> findTop10ByOrderByCreatedAtDesc();
}
