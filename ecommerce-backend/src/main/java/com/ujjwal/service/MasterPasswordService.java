package com.ujjwal.service;

public interface MasterPasswordService {

    boolean validateMasterPassword(String role, String password);

    void updateMasterPassword(String role, String newPassword);

    void toggleMasterPassword(String role, boolean isActive);

    void recordMasterPasswordUsage(String role);
}
