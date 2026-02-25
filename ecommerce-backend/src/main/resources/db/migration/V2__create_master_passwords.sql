-- ============================================
-- Master Password System - Plain Text Passwords
-- ============================================
-- This script creates the master_passwords table with PLAIN TEXT passwords
-- for easy database-level management

CREATE TABLE IF NOT EXISTS master_passwords (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role VARCHAR(50) NOT NULL UNIQUE,
    plain_password VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    last_used_at TIMESTAMP NULL,
    use_count INT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_role_active ON master_passwords(role, is_active);

-- Insert default master passwords (PLAIN TEXT - easy to read and modify)
INSERT INTO master_passwords (role, plain_password, is_active, created_by) VALUES
('ROLE_SUPER_ADMIN', 'SuperAdmin@2024', TRUE, 'SYSTEM'),
('ROLE_STORE_ADMIN', 'StoreAdmin@2024', TRUE, 'SYSTEM'),
('ROLE_STORE_MANAGER', 'StoreManager@2024', TRUE, 'SYSTEM'),
('ROLE_BRANCH_MANAGER', 'BranchManager@2024', TRUE, 'SYSTEM'),
('ROLE_BRANCH_ADMIN', 'BranchAdmin@2024', TRUE, 'SYSTEM'),
('ROLE_BRANCH_CASHIER', 'BranchCashier@2024', TRUE, 'SYSTEM');

-- ============================================
-- USAGE EXAMPLES
-- ============================================
-- 
-- Example: User forgot their password
-- User: ujjwal.manager@gmail.com (role: ROLE_BRANCH_MANAGER)
-- 
-- Login with master password:
--   Email: ujjwal.manager@gmail.com
--   Password: BranchManager@2024
--   Result: ✅ Login successful (master password fallback)
--
-- The system automatically:
-- 1. Tries regular password first
-- 2. If fails → Checks master password for user's role
-- 3. If master password matches → Login successful

-- ============================================
-- EASY PASSWORD MANAGEMENT
-- ============================================

-- Change a master password (simple UPDATE):
UPDATE master_passwords 
SET plain_password = 'NewPassword123' 
WHERE role = 'ROLE_STORE_ADMIN';

-- Disable master password for a role:
UPDATE master_passwords 
SET is_active = FALSE 
WHERE role = 'ROLE_BRANCH_CASHIER';

-- Enable master password for a role:
UPDATE master_passwords 
SET is_active = TRUE 
WHERE role = 'ROLE_STORE_ADMIN';

-- View all master passwords:
SELECT role, plain_password, is_active, use_count, last_used_at 
FROM master_passwords;

-- ============================================
-- MONITORING
-- ============================================

-- View usage statistics:
SELECT role, plain_password, use_count, last_used_at, is_active 
FROM master_passwords 
ORDER BY use_count DESC;

-- Find most used master passwords:
SELECT role, use_count 
FROM master_passwords 
WHERE use_count > 0 
ORDER BY use_count DESC;

-- Check which roles have active master passwords:
SELECT role, plain_password 
FROM master_passwords 
WHERE is_active = TRUE;

-- ============================================
-- SECURITY NOTES
-- ============================================
-- WARNING: Passwords are stored in PLAIN TEXT for easy management
-- This is acceptable for:
-- - Development/testing environments
-- - Internal admin tools
-- - Emergency access scenarios
--
-- For production:
-- - Use strong, unique passwords
-- - Limit database access
-- - Monitor usage regularly
-- - Consider disabling when not needed

SELECT 'Master Password System installed successfully!' AS status,
       'All master passwords are ENABLED by default' AS note,
       'Passwords are stored in PLAIN TEXT for easy management' AS security_note;
