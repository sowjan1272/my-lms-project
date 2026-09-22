Write-Host "Setting up environment..." -ForegroundColor Cyan

$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12.1"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "student_management"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "root"
$env:JWT_SECRET = "lwqhHODpjooKkfPHqpvFvyQcNdxl3kAmPCGkj2h5Nz9Z9j8HUddOkB8abFVdBSfF"
$env:CORS_ORIGIN = "http://localhost:5173"

Set-Location "C:\Users\Admin\Desktop\student management\student management\backend"

Write-Host "Starting backend (Java 21, dev profile)..." -ForegroundColor Cyan
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
