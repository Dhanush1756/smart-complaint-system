@echo off
echo.
echo =====================================================
echo   SMART PUBLIC COMPLAINT MANAGEMENT SYSTEM
echo   Starting backend...
echo =====================================================
echo.

cd /d "%~dp0backend"

java -version >nul 2>&1
IF ERRORLEVEL 1 (
    echo ERROR: Java not found. Install Java 17+
    pause
    exit /b 1
)

mvn -version >nul 2>&1
IF ERRORLEVEL 1 (
    echo ERROR: Maven not found. Install Maven 3.8+
    pause
    exit /b 1
)

echo Building...
mvn clean package -q -DskipTests

IF ERRORLEVEL 1 (
    echo Build FAILED. See errors above.
    pause
    exit /b 1
)

echo Build OK!
echo.
echo Starting server on http://localhost:8080
echo Open frontend\index.html in your browser
echo.
echo Logins:  admin/admin123  citizen1/citizen123  officer1/officer123
echo.
mvn spring-boot:run
pause
