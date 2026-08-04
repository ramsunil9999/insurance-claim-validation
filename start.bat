@echo off
set PROJECT_ROOT=%~dp0
cd /d "%PROJECT_ROOT%"
start "Backend" cmd /k "cd /d "%PROJECT_ROOT%" && .\mvnw spring-boot:run"
start "Frontend" cmd /k "cd /d "%PROJECT_ROOT%\frontend" && npm run dev"
