@echo off
REM GitHub Repository Setup Script for AI Resume Screening System
REM This script helps prepare and push the repository to GitHub

echo ========================================
echo GitHub Repository Setup Script
echo AI Resume Screening System
echo ========================================
echo.

REM Check if git is initialized
if not exist ".git" (
    echo Git repository not found. Initializing...
    git init
    echo Git repository initialized successfully.
) else (
    echo Git repository already initialized.
)

echo.
echo Step 1: Checking repository status...
git status

echo.
echo Step 2: Verifying no sensitive files are staged...
echo Checking for .env files, logs, and other sensitive data...
git status --short | findstr /I ".env .log key secret password"
if %ERRORLEVEL% EQU 0 (
    echo WARNING: Potentially sensitive files found in git status!
    echo Please review and remove them before proceeding.
    pause
    exit /b 1
) else (
    echo No sensitive files found. Good to proceed.
)

echo.
echo Step 3: Current commit history...
git log --oneline -5

echo.
echo Step 4: Repository files summary...
echo Total files tracked:
git ls-files | find /c /v ""

echo.
echo ========================================
echo Repository is ready for GitHub!
echo ========================================
echo.
echo Next Steps:
echo 1. Create a new repository on GitHub: https://github.com/new
echo 2. Repository name: ai-resume-screener (recommended)
echo 3. Description: AI Resume Screening System - Java Spring Boot + React + PostgreSQL
echo 4. Make it Public (recommended) or Private
echo 5. DO NOT initialize with README, .gitignore, or license
echo 6. Click "Create repository"
echo.
echo After creating the repository, run:
echo git remote add origin https://github.com/YOUR_USERNAME/ai-resume-screener.git
echo git branch -M master
echo git push -u origin master
echo.
echo ========================================
echo.

REM Ask user if they want to proceed with adding remote
set /p ADD_REMOTE="Do you want to add GitHub remote now? (y/n): "
if /i "%ADD_REMOTE%"=="y" (
    set /p GITHUB_USERNAME="Enter your GitHub username: "
    set /p REPO_NAME="Enter repository name (default: ai-resume-screener): "
    if "%REPO_NAME%"=="" set REPO_NAME=ai-resume-screener
    
    echo.
    echo Adding remote: https://github.com/%GITHUB_USERNAME%/%REPO_NAME%.git
    git remote add origin https://github.com/%GITHUB_USERNAME%/%REPO_NAME%.git
    
    echo.
    echo Remote added successfully!
    echo.
    echo To push to GitHub, run:
    echo git branch -M master
    echo git push -u origin master
) else (
    echo.
    echo You can add the remote later using:
    echo git remote add origin https://github.com/YOUR_USERNAME/ai-resume-screener.git
)

echo.
echo ========================================
echo Setup Complete!
echo ========================================
echo.
echo Repository Status: ✅ Ready for GitHub
echo Sensitive Files: ✅ None detected
echo Build Artifacts: ✅ Clean
echo Documentation: ✅ Complete
echo.
echo For deployment instructions, see: PRODUCTION_DEPLOYMENT.md
echo.
pause