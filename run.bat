@echo off
title Library Management System
echo ================================
echo   COMPILING JAVA SOURCE FILES
echo ================================
echo.

REM Create bin directory if it doesn’t exist
if not exist bin mkdir bin

REM Compile all .java files from src to bin
javac -d bin src\*.java

if %errorlevel% neq 0 (
    echo.
    echo ❌ Compilation failed! Please fix the errors above.
    pause
    exit /b
)

echo.
echo ✅ Compilation successful!
echo ================================
echo   RUNNING LIBRARY MANAGEMENT SYSTEM
echo ================================
echo.

REM Run the main class
java -cp bin src.Main

echo.
echo ================================
echo Program finished.
pause
