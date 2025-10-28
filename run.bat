@echo off
echo 1. Compiling Java source files...
javac src/*.java

REM Check if compilation was successful
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed. Please review the errors.
    pause
    exit /b
)

echo 2. Compilation successful. Running the application...
echo.
java -cp src src.Main

echo.
echo Application closed.
pause