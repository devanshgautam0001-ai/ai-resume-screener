# Production Deployment Guide

This guide covers deploying the AI Resume Screening System to production using various platforms.

## Table of Contents

1. [Platform Options](#platform-options)
2. [Environment Variables](#environment-variables)
3. [Vercel + Render Deployment](#vercel--render-deployment)
4. [Docker Deployment](#docker-deployment)
5. [Manual Deployment](#manual-deployment)
6. [Post-Deployment Checklist](#post-deployment-checklist)

## Platform Options

### Recommended Stack
- **Frontend**: Vercel (React/Vite)
- **Backend**: Render (Spring Boot)
- **Database**: Render PostgreSQL

### Alternative Stack
- **Full Docker**: Docker Compose on any VPS
- **AWS**: ECS + RDS + S3 + CloudFront
- **Railway**: Full stack deployment

## Environment Variables

### Required Environment Variables

Copy `.env.example` to `.env` and configure the following:

#### Backend Variables
```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/resume_ai
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_secure_password
APP_FRONTEND_URL=https://your-frontend.vercel.app
APP_JWT_SECRET=your_generated_jwt_secret
APP_JWT_EXPIRATION_MS=86400000
APP_STORAGE_UPLOAD_DIR=/tmp/uploads
```

#### Frontend Variables
```bash
VITE_API_BASE_URL=https://your-backend.onrender.com/api
```

### Security Notes
- Generate a secure JWT secret: `openssl rand -base64 32`
- Never commit actual secrets to git
- Use platform-specific secret management
- Rotate secrets regularly

## Vercel + Render Deployment

### Step 1: Deploy Backend to Render

1. **Create Render Account**
   - Go to [render.com](https://render.com) and sign up
   - Create a new web service

2. **Configure Render Service**
   - Connect your GitHub repository
   - Set build command: `mvn clean package -DskipTests`
   - Set start command: `java -jar target/backend-1.0.0.jar`
   - Select instance type: Free (for testing) or Standard (for production)

3. **Add Environment Variables**
   ```
   SPRING_PROFILES_ACTIVE=prod
   SPRING_DATASOURCE_URL=[From Render PostgreSQL]
   SPRING_DATASOURCE_USERNAME=[From Render PostgreSQL]
   SPRING_DATASOURCE_PASSWORD=[From Render PostgreSQL]
   APP_FRONTEND_URL=https://your-frontend.vercel.app
   APP_JWT_SECRET=[Generate secure secret]
   APP_JWT_EXPIRATION_MS=86400000
   APP_STORAGE_UPLOAD_DIR=/tmp/uploads
   ```

4. **Create PostgreSQL Database**
   - In Render, create a new PostgreSQL database
   - Copy the connection string to backend environment variables
   - Note: Render provides internal DNS for database connections

5. **Deploy Backend**
   - Click "Deploy Web Service"
   - Wait for build and deployment to complete
   - Note your backend URL: `https://your-backend.onrender.com`

6. **Initialize Database Schema**
   - Access your Render PostgreSQL database
   - Run the schema from `backend/src/main/resources/schema-postgresql.sql`
   - Or let Hibernate auto-create tables (set `ddl-auto=update` temporarily)

### Step 2: Deploy Frontend to Vercel

1. **Create Vercel Account**
   - Go to [vercel.com](https://vercel.com) and sign up
   - Install Vercel CLI: `npm i -g vercel`

2. **Configure Frontend**
   - Update `frontend/.env.production`:
     ```bash
     VITE_API_BASE_URL=https://your-backend.onrender.com/api
     ```

3. **Deploy to Vercel**
   ```bash
   cd frontend
   vercel
   ```
   - Follow the prompts
   - Set project name
   - Confirm build settings

4. **Add Environment Variable in Vercel Dashboard**
   - Go to project settings
   - Add `VITE_API_BASE_URL` with your Render backend URL
   - Redeploy the project

5. **Configure Custom Domain (Optional)**
   - In Vercel dashboard, add custom domain
   - Update DNS records
   - Update `APP_FRONTEND_URL` in Render backend

### Step 3: Update CORS Configuration

1. **Update Backend CORS**
   - In Render dashboard, update environment variable:
     ```
     APP_FRONTEND_URL=https://your-frontend.vercel.app
     ```
   - Redeploy backend to apply changes

2. **Test CORS**
   - Open browser DevTools
   - Test API calls from frontend
   - Check for CORS errors

## Docker Deployment

### Prerequisites
- Docker installed
- Docker Compose installed
- PostgreSQL database (external or via Docker)

### Step 1: Configure Environment

Create `.env` file in project root:
```bash
POSTGRES_PASSWORD=your_secure_password
SPRING_PROFILES_ACTIVE=prod
APP_FRONTEND_URL=https://your-domain.com
APP_JWT_SECRET=your_generated_jwt_secret
VITE_API_BASE_URL=https://your-domain.com/api
```

### Step 2: Build and Run

```bash
# Build images
docker-compose build

# Start services
docker-compose up -d

# Check logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Step 3: Configure Reverse Proxy (Optional)

For production, use nginx reverse proxy:

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location / {
        proxy_pass http://frontend:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Step 4: SSL Configuration

Use Let's Encrypt with Certbot:

```bash
# Install certbot
sudo apt-get install certbot python3-certbot-nginx

# Generate SSL certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal is configured automatically
```

## Manual Deployment

### Backend Deployment

1. **Build Application**
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```

2. **Transfer to Server**
   ```bash
   scp target/backend-1.0.0.jar user@server:/opt/ai-resume-screener/
   ```

3. **Configure Systemd Service**
   ```ini
   # /etc/systemd/system/ai-resume-screener.service
   [Unit]
   Description=AI Resume Screener Backend
   After=network.target

   [Service]
   Type=simple
   User=appuser
   WorkingDirectory=/opt/ai-resume-screener
   EnvironmentFile=/opt/ai-resume-screener/.env
   ExecStart=/usr/bin/java -jar backend-1.0.0.jar
   Restart=always

   [Install]
   WantedBy=multi-user.target
   ```

4. **Start Service**
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl start ai-resume-screener
   sudo systemctl enable ai-resume-screener
   ```

### Frontend Deployment

1. **Build Application**
   ```bash
   cd frontend
   npm run build
   ```

2. **Deploy to Web Server**
   ```bash
   # Copy dist folder to web server
   scp -r dist/* user@server:/var/www/ai-resume-screener/
   ```

3. **Configure Nginx**
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       root /var/www/ai-resume-screener;
       index index.html;

       location / {
           try_files $uri $uri/ /index.html;
       }
   }
   ```

## Post-Deployment Checklist

### Security
- [ ] Change all default passwords
- [ ] Enable SSL/TLS certificates
- [ ] Configure firewall rules
- [ ] Set up automatic backups
- [ ] Enable security headers
- [ ] Review CORS configuration

### Performance
- [ ] Enable gzip compression
- [ ] Configure CDN for static assets
- [ ] Set up caching headers
- [ ] Monitor resource usage
- [ ] Configure database connection pooling

### Monitoring
- [ ] Set up application monitoring (e.g., Sentry, LogRocket)
- [ ] Configure error tracking
- [ ] Set up uptime monitoring
- [ ] Configure log aggregation
- [ ] Set up alerting

### Functionality
- [ ] Test user registration/login
- [ ] Test resume upload functionality
- [ ] Test AI screening analysis
- [ ] Test PDF export
- [ ] Verify database connections
- [ ] Test file upload limits
- [ ] Verify email notifications (if configured)

### Backup
- [ ] Configure database backups
- [ ] Backup uploaded resumes
- [ ] Document recovery procedures
- [ ] Test backup restoration

## Troubleshooting

### Common Issues

1. **CORS Errors**
   - Verify `APP_FRONTEND_URL` matches deployed frontend URL
   - Check browser console for specific CORS errors
   - Ensure backend allows credentials

2. **Database Connection Issues**
   - Verify database is accessible from backend
   - Check connection string format
   - Ensure database user has proper permissions

3. **File Upload Failures**
   - Check file size limits (default 10MB)
   - Verify upload directory permissions
   - Check disk space on server

4. **Memory Issues**
   - Increase JVM memory: `-Xmx1g`
   - Optimize database queries
   - Enable connection pooling

5. **Build Failures**
   - Clear Docker cache: `docker system prune -a`
   - Verify all dependencies are installed
   - Check for version conflicts

## Maintenance

### Regular Tasks
- Update dependencies monthly
- Review and rotate secrets quarterly
- Monitor disk usage and clean up old files
- Review and optimize database performance
- Check for security vulnerabilities

### Scaling Considerations
- Add load balancer for high traffic
- Implement caching layer (Redis)
- Use CDN for static assets
- Consider read replicas for database
- Implement queue system for heavy processing

## Support

For issues or questions:
- Check application logs
- Review platform-specific documentation
- Monitor error tracking dashboards
- Review database performance metrics