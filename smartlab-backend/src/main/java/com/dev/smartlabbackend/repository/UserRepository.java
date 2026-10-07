package com.dev.smartlabbackend.repository;

import com.dev.smartlabbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByOrgEmail(String orgEmail);

    @Query("SELECT u FROM User u WHERE LOWER(u.orgEmail) = LOWER(:orgEmail) AND u.deletedAt IS NULL")
    Optional<User> findActiveByOrgEmail(@Param("orgEmail") String orgEmail);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByOrgEmail(String orgEmail);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.orgEmail) = LOWER(:orgEmail) AND u.id <> :id")
    boolean existsByOrgEmailAndIdNot(@Param("orgEmail") String orgEmail, @Param("id") Long id);

    Page<User> findByFullNameContainingIgnoreCase(String name, Pageable pageable);
}
