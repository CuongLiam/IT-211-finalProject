package com.example.backend.repository;

import com.example.backend.entity.User;
import com.example.backend.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

    // paginate + search
    Page<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String fullNameKeyword, String emailKeyword, Pageable pageable);
    Page<User> findByRoleAndFullNameContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
            Role role1, String fullNameKeyword, Role role2, String emailKeyword, Pageable pageable
    );
}