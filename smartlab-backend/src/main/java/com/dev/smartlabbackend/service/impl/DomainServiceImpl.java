package com.dev.smartlabbackend.service.impl;

import com.dev.smartlabbackend.dto.request.CreateDomainRequest;
import com.dev.smartlabbackend.dto.request.UpdateDomainRequest;
import com.dev.smartlabbackend.dto.response.DomainResponse;
import com.dev.smartlabbackend.entity.Domain;
import com.dev.smartlabbackend.entity.User;
import com.dev.smartlabbackend.exception.ConflictException;
import com.dev.smartlabbackend.exception.ResourceNotFoundException;
import com.dev.smartlabbackend.mapper.DomainMapper;
import com.dev.smartlabbackend.repository.DomainRepository;
import com.dev.smartlabbackend.repository.UserRepository;
import com.dev.smartlabbackend.service.DomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;
    private final DomainMapper domainMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DomainResponse createDomain(Long currentUserId, CreateDomainRequest request) {
        // 1. Chuẩn hoá input (trim; description rỗng -> null)
        request.setName(request.getName().trim());
        request.setDescription(StringUtils.hasText(request.getDescription())
                ? request.getDescription().trim()
                : null);

        // 2. Kiểm tra nghiệp vụ: tên domain là duy nhất (không phân biệt hoa thường)
        if (domainRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ConflictException("Domain name already exists: " + request.getName());
        }
        User creator = userRepository.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        // 3. Map request -> entity (mapper set status = ACTIVE)
        Domain domain = domainMapper.toEntity(request);
        domain.setCreatedBy(creator);

        // 4. Lưu; bắt thêm trường hợp 2 request tạo cùng tên đồng thời (UNIQUE ở DB)
        try {
            domain = domainRepository.saveAndFlush(domain);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Domain name already exists: " + request.getName());
        }

        log.info("Created domain id={} name={}", domain.getId(), domain.getName());

        // 5. Map entity -> response DTO
        return domainMapper.toResponse(domain);
    }

    @Override
    @Transactional
    public DomainResponse updateDomain(Long currentUserId, Long id, UpdateDomainRequest request) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found with id: " + id));

        User updatedBy = userRepository.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        // Request -> Entity
        domainMapper.updateEntity(request, domain);
        domain.setUpdatedBy(updatedBy);

        domain = domainRepository.saveAndFlush(domain);

        // Entity -> Response
        return domainMapper.updateResponse(domain);
    }

    @Transactional
    @Override
    public DomainResponse softDeleteDomain(Long currentUserId, Long id) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found with id: " + id));

        User updatedBy = userRepository.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
        domain.setIsDeleted(true);
        domain.setUpdatedBy(updatedBy);
        domainRepository.save(domain);
        return domainMapper.softDelete(domain);
    }

    @Transactional
    @Override
    public Page<DomainResponse> getListDomain(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Domain> domains = domainRepository.findAll(pageable);
        if (search != null && !search.isEmpty()) {
            domains = domainRepository.findByNameContainingIgnoreCase(search, pageable);
        }
        return domains.map(domainMapper::toResponse);
    }
}
