package com.dev.smartlabbackend.repository;

import com.dev.smartlabbackend.entity.Project;
import com.dev.smartlabbackend.entity.ProjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectTypeRepository extends JpaRepository<ProjectType, Long> {
    Optional<ProjectType> findByIdAndDeletedAtIsNull(Long aLong);

}
