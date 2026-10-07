package com.dev.smartlabbackend.repository;

import com.dev.smartlabbackend.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByCode(String code);

    //hàm check project trùng tên project nào khác trong cùng một domain hay ko
    boolean existsByDomainIdAndNameIgnoreCaseAndDeletedAtIsNull(Long domainId, String name);

    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Project> findByIdAndDeletedAtIsNull(Long id);
}
