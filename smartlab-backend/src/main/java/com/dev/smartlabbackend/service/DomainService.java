package com.dev.smartlabbackend.service;

import com.dev.smartlabbackend.dto.request.CreateDomainRequest;
import com.dev.smartlabbackend.dto.request.UpdateDomainRequest;
import com.dev.smartlabbackend.dto.response.DomainResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface DomainService {

    DomainResponse createDomain(Long currentUserId, CreateDomainRequest request);

    DomainResponse updateDomain(Long currentUserId, Long id, UpdateDomainRequest request);

    DomainResponse softDeleteDomain(Long currentUserId, Long id);

    Page<DomainResponse> getListDomain(int page, int size, String search);
}
