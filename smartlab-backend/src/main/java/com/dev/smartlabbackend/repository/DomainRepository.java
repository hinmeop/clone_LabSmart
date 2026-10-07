package com.dev.smartlabbackend.repository;

import com.dev.smartlabbackend.entity.Domain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long> {

    /**
     * Kiểm tra trùng tên, không phân biệt hoa thường ("AI" và "ai" coi là trùng).
     */
    boolean existsByNameIgnoreCase(String name);

    Page<Domain> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Domain> findByIdAndDeletedAtIsNull(Long id);

}
