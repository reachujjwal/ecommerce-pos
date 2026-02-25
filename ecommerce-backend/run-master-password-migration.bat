@echo off
REM Quick SQL Execution Script for Master Password System

echo ============================================
echo Master Password System - Database Setup
echo ============================================
echo.

REM Get database credentials from user
set /p DB_HOST="Enter MySQL host (default: localhost): "
if "%DB_HOST%"=="" set DB_HOST=localhost

set /p DB_PORT="Enter MySQL port (default: 3306): "
if "%DB_PORT%"=="" set DB_PORT=3306

set /p DB_NAME="Enter database name (default: e_commerce): "
if "%DB_NAME%"=="" set DB_NAME=e_commerce

set /p DB_USER="Enter MySQL username (default: root): "
if "%DB_USER%"=="" set DB_USER=root

set /p DB_PASS="Enter MySQL password: "

echo.
echo Connecting to MySQL...
echo Host: %DB_HOST%:%DB_PORT%
echo Database: %DB_NAME%
echo User: %DB_USER%
echo.

REM Execute SQL script
mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% < "src\main\resources\db\migration\V2__create_master_passwords.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================
    echo SUCCESS! Master password table created
    echo ============================================
    echo.
    echo Default passwords:
    echo - ROLE_SUPER_ADMIN: SuperAdmin@2024
    echo - ROLE_STORE_ADMIN: StoreAdmin@2024
    echo - ROLE_STORE_MANAGER: StoreManager@2024
    echo - ROLE_BRANCH_MANAGER: BranchManager@2024
    echo - ROLE_BRANCH_ADMIN: BranchAdmin@2024
    echo - ROLE_BRANCH_CASHIER: BranchCashier@2024
    echo.
    echo All master passwords are ENABLED by default.
    echo.
    echo Test login with:
    echo   Email: ujjwal.manager@gmail.com
    echo   Password: BranchManager@2024
    echo.
) else (
    echo.
    echo ============================================
    echo ERROR: Failed to execute SQL script
    echo ============================================
    echo.
    echo Please check:
    echo 1. MySQL is running
    echo 2. Database credentials are correct
    echo 3. Database '%DB_NAME%' exists
    echo.
)

pause
