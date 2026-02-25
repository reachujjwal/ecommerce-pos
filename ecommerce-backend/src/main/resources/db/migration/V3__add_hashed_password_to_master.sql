-- Add password_hash column to master_passwords table
ALTER TABLE master_passwords ADD COLUMN password_hash VARCHAR(255) AFTER plain_password;

-- Update existing rows
UPDATE master_passwords SET password_hash = plain_password;
