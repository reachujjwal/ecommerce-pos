package com.ujjwal.service.impl;

import com.ujjwal.modal.MasterPassword;
import com.ujjwal.repository.MasterPasswordRepository;
import com.ujjwal.service.MasterPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MasterPasswordServiceImpl implements MasterPasswordService {

    private final MasterPasswordRepository masterPasswordRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public boolean validateMasterPassword(String role, String password) {
        Optional<MasterPassword> masterPassword = masterPasswordRepository.findByRoleAndIsActive(role, true);

        if (masterPassword.isEmpty()) {
            System.out.println("❌ Master password not found or inactive for role: " + role);
            return false;
        }

        MasterPassword mp = masterPassword.get();
        boolean isValid = false;

        // Try hashed comparison first if hash is present
        if (mp.getPasswordHash() != null) {
            isValid = passwordEncoder.matches(password, mp.getPasswordHash());
        }

        // If not valid or hash missing, fallback/backup check with plain text
        if (!isValid) {
            isValid = password.equals(mp.getPlainPassword());
        }

        if (isValid) {
            System.out.println("✅ Master password validated for role: " + role);
            recordMasterPasswordUsage(role);
        } else {
            System.out.println("❌ Invalid master password for role: " + role);
        }

        return isValid;
    }

    @Override
    @Transactional
    public void recordMasterPasswordUsage(String role) {
        masterPasswordRepository.findByRole(role).ifPresent(mp -> {
            mp.setLastUsedAt(LocalDateTime.now());
            Integer currentCount = mp.getUseCount();
            mp.setUseCount(currentCount == null ? 1 : currentCount + 1);
            masterPasswordRepository.save(mp);
            System.out.println("📊 Master password usage recorded for " + role + " (count: " + mp.getUseCount() + ")");
        });
    }

    @Override
    @Transactional
    public void updateMasterPassword(String role, String newPassword) {
        MasterPassword mp = masterPasswordRepository.findByRole(role)
                .orElseThrow(() -> new RuntimeException("Master password not found for role: " + role));

        mp.setPlainPassword(newPassword);
        mp.setPasswordHash(passwordEncoder.encode(newPassword));
        masterPasswordRepository.save(mp);
        System.out.println("🔐 Master password updated for role: " + role + " (Plain + Hashed)");
    }

    @Override
    @Transactional
    public void toggleMasterPassword(String role, boolean isActive) {
        MasterPassword mp = masterPasswordRepository.findByRole(role)
                .orElseThrow(() -> new RuntimeException("Master password not found for role: " + role));

        mp.setIsActive(isActive);
        masterPasswordRepository.save(mp);
        System.out.println("🔄 Master password " + (isActive ? "enabled" : "disabled") + " for role: " + role);
    }
}
