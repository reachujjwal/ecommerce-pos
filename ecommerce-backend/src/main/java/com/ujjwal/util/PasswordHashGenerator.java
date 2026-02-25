package com.ujjwal.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes for master passwords
 * Run this class to generate hashes for the SQL migration script
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        System.out.println("=== Master Password BCrypt Hashes ===\n");

        // Generate hashes for all role master passwords
        String[] roles = {
                "ROLE_SUPER_ADMIN:SuperAdmin@2024",
                "ROLE_STORE_ADMIN:StoreAdmin@2024",
                "ROLE_STORE_MANAGER:StoreManager@2024",
                "ROLE_BRANCH_MANAGER:BranchManager@2024",
                "ROLE_BRANCH_ADMIN:BranchAdmin@2024",
                "ROLE_BRANCH_CASHIER:BranchCashier@2024"
        };

        for (String rolePassword : roles) {
            String[] parts = rolePassword.split(":");
            String role = parts[0];
            String password = parts[1];
            String hash = passwordEncoder.encode(password);

            System.out.println("-- " + role + " - Password: " + password);
            System.out.println("INSERT INTO master_passwords (role, password_hash, is_active, created_by) VALUES");
            System.out.println("('" + role + "', '" + hash + "', FALSE, 'SYSTEM');");
            System.out.println();
        }

        System.out.println("\n=== Copy the INSERT statements above into your SQL migration file ===");
        System.out.println("=== Remember to set is_active = TRUE for roles you want to enable ===");
    }
}
