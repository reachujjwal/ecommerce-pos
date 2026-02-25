# PowerShell script to run Master Password migration
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Master Password System - Database Setup" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Database credentials from application.yml
$dbHost = "localhost"
$dbPort = "3306"
$dbName = "e_commerce"
$dbUser = "root"
$dbPass = "root"

Write-Host "Database Configuration:" -ForegroundColor Yellow
Write-Host "  Host: $dbHost:$dbPort"
Write-Host "  Database: $dbName"
Write-Host "  User: $dbUser"
Write-Host ""

# SQL file path
$sqlFile = "src\main\resources\db\migration\V2__create_master_passwords.sql"

if (-Not (Test-Path $sqlFile)) {
    Write-Host "ERROR: SQL file not found at $sqlFile" -ForegroundColor Red
    exit 1
}

Write-Host "Executing SQL migration..." -ForegroundColor Green
Write-Host ""

# Read and execute SQL
try {
    $sqlContent = Get-Content $sqlFile -Raw
    
    # Using mysql command if available
    $mysqlPath = Get-Command mysql -ErrorAction SilentlyContinue
    
    if ($mysqlPath) {
        mysql -h $dbHost -P $dbPort -u $dbUser -p$dbPass $dbName -e $sqlContent
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host ""
            Write-Host "============================================" -ForegroundColor Green
            Write-Host "SUCCESS! Master password table created" -ForegroundColor Green
            Write-Host "============================================" -ForegroundColor Green
            Write-Host ""
            Write-Host "Default passwords:" -ForegroundColor Yellow
            Write-Host "  ROLE_SUPER_ADMIN: SuperAdmin@2024"
            Write-Host "  ROLE_STORE_ADMIN: StoreAdmin@2024"
            Write-Host "  ROLE_STORE_MANAGER: StoreManager@2024"
            Write-Host "  ROLE_BRANCH_MANAGER: BranchManager@2024"
            Write-Host "  ROLE_BRANCH_ADMIN: BranchAdmin@2024"
            Write-Host "  ROLE_BRANCH_CASHIER: BranchCashier@2024"
            Write-Host ""
            Write-Host "All master passwords are ENABLED by default." -ForegroundColor Green
            Write-Host ""
            Write-Host "Test login with:" -ForegroundColor Cyan
            Write-Host "  Email: ujjwal.manager@gmail.com"
            Write-Host "  Password: BranchManager@2024"
            Write-Host ""
        }
    } else {
        Write-Host "MySQL command not found. Please run SQL manually:" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "1. Open MySQL Workbench or command line"
        Write-Host "2. Connect to database: $dbName"
        Write-Host "3. Run the SQL file: $sqlFile"
        Write-Host ""
    }
} catch {
    Write-Host ""
    Write-Host "ERROR: Failed to execute SQL" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
}
