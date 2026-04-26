@echo off
REM AI Resume Screener - Local Production Simulation Script
REM This script helps test the production build locally

echo 🚀 Starting Local Production Build Test...

echo Step 1: Building Backend for Production
cd backend
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Backend build failed
    exit /b 1
)
echo ✅ Backend build successful

echo Step 2: Building Frontend for Production
cd ..\frontend
call npm run build
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Frontend build failed
    exit /b 1
)
echo ✅ Frontend build successful

cd ..
echo ✅ Production builds completed successfully!
echo.
echo Backend JAR location: backend\target\backend-1.0.0.jar
echo Frontend build location: frontend\dist\
echo.
echo To test the production build locally:
echo 1. Backend: cd backend ^&^& java -jar target\backend-1.0.0.jar --spring.profiles.active=prod
echo 2. Frontend: Use nginx or serve the frontend\dist directory
echo.
echo To deploy to production:
echo 1. Follow the instructions in PRODUCTION_DEPLOYMENT.md
echo 2. Ensure all environment variables are set
echo 3. Deploy to Render (backend) and Vercel (frontend)

pause