package com.ujjwal.repository;

import com.ujjwal.modal.MasterPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterPasswordRepository extends JpaRepository<MasterPassword, Long> {

    Optional<MasterPassword> findByRoleAndIsActive(String role, Boolean isActive);

    Optional<MasterPassword> findByRole(String role);

    List<MasterPassword> findByIsActive(Boolean isActive);
}
