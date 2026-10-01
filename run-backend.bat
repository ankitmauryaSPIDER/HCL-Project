@echo off
cd /d "%~dp0backend"
echo Starting PortfolioPro backend...
mvn spring-boot:run
pause
