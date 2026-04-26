#!/bin/bash

# Production Deployment Script
# This script helps deploy the application to production

set -e

echo "🚀 AI Resume Screening System - Production Deployment"
echo "====================================================="

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
    echo "✅ Environment variables loaded from .env"
else
    echo "⚠️  Warning: .env file not found. Using default values."
fi

# Function to deploy backend
deploy_backend() {
    echo ""
    echo "📦 Deploying Backend..."
    echo "-----------------------"
    
    cd backend
    
    # Build the application
    echo "Building Spring Boot application..."
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo "✅ Backend build successful"
    else
        echo "❌ Backend build failed"
        exit 1
    fi
    
    cd ..
}

# Function to deploy frontend
deploy_frontend() {
    echo ""
    echo "🎨 Deploying Frontend..."
    echo "-----------------------"
    
    cd frontend
    
    # Install dependencies
    echo "Installing dependencies..."
    npm ci
    
    # Build for production
    echo "Building for production..."
    npm run build
    
    if [ $? -eq 0 ]; then
        echo "✅ Frontend build successful"
        echo "📁 Build output: dist/"
    else
        echo "❌ Frontend build failed"
        exit 1
    fi
    
    cd ..
}

# Function to build Docker images
build_docker() {
    echo ""
    echo "🐳 Building Docker Images..."
    echo "---------------------------"
    
    # Build all services
    docker-compose build
    
    if [ $? -eq 0 ]; then
        echo "✅ Docker images built successfully"
    else
        echo "❌ Docker build failed"
        exit 1
    fi
}

# Function to run database migrations
run_migrations() {
    echo ""
    echo "🗄️  Running Database Migrations..."
    echo "---------------------------------"
    
    if [ -f scripts/init-db.sh ]; then
        chmod +x scripts/init-db.sh
        ./scripts/init-db.sh
    else
        echo "⚠️  Migration script not found. Skipping..."
    fi
}

# Main deployment flow
echo "Select deployment option:"
echo "1) Full Build (Backend + Frontend)"
echo "2) Docker Build"
echo "3) Backend Only"
echo "4) Frontend Only"
echo "5) Database Migrations Only"
echo "6) Full Production Deployment (Build + Docker + Migrations)"
read -p "Enter choice (1-6): " choice

case $choice in
    1)
        deploy_backend
        deploy_frontend
        echo ""
        echo "✅ Full build completed successfully!"
        ;;
    2)
        build_docker
        echo ""
        echo "✅ Docker build completed successfully!"
        echo "Run 'docker-compose up -d' to start services"
        ;;
    3)
        deploy_backend
        echo ""
        echo "✅ Backend deployment completed successfully!"
        ;;
    4)
        deploy_frontend
        echo ""
        echo "✅ Frontend deployment completed successfully!"
        ;;
    5)
        run_migrations
        echo ""
        echo "✅ Database migrations completed successfully!"
        ;;
    6)
        deploy_backend
        deploy_frontend
        build_docker
        run_migrations
        echo ""
        echo "✅ Full production deployment completed successfully!"
        echo "Run 'docker-compose up -d' to start all services"
        ;;
    *)
        echo "❌ Invalid choice. Exiting."
        exit 1
        ;;
esac

echo ""
echo "🎉 Deployment process completed!"
echo ""
echo "Next steps:"
echo "- Update environment variables with production values"
echo "- Configure CORS settings with your production domain"
echo "- Set up SSL certificates"
echo "- Configure monitoring and logging"
echo "- Test all functionality"
echo ""
echo "For detailed deployment instructions, see DEPLOYMENT.md"