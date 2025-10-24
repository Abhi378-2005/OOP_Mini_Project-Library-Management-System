@echo off
title Build & Run Library Management System
echo Cleaning old build...
if exist LibraryManagementSystem.jar del LibraryManagementSystem.jar
if not exist bin mkdir bin

echo Compiling Java files...
javac -d bin src\*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b
)

echo Creating manifest...
echo Main-Class: src.Main>manifest.txt
echo Class-Path: data/>>manifest.txt
echo.>>manifest.txt

echo Packaging JAR...
jar cfm LibraryManagementSystem.jar manifest.txt -C bin .

echo Running application...
java -jar LibraryManagementSystem.jar
pause
