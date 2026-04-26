# Production Deployment Guide - AI Resume Screening System

## Prerequisites
- GitHub account
- Vercel account (for frontend)
- Render account (for backend + database)
- PostgreSQL database credentials

## Step 1: GitHub Repository Setup

1. Create a new GitHub repository named `ai-resume-screener`
2. Add it as remote:
```bash
git remote add origin https://github.com/YOUR_USERNAME/ai-resume-screener.git
```

3. Commit and push:
```bash
git add .
git commit -m "Production-ready deployment setup"
git push -u origin master
```

## Step 2: PostgreSQL Database Setup (Render)

1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click "New" → "Database" → "PostgreSQL"
3. Configure:
   - Name: `ai-resume-screener-db`
   - Database: `resume_ai`
   - User: `resume_ai_user`
4. Click "Create Database"
5. Save the connection credentials (Internal Database URL)

## Step 3: Backend Deployment (Render)

1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click "New" → "Web Service"
3. Connect to your GitHub repository
4. Configure:
   - Name: `ai-resume-screener-backend`
   - Environment: `Java`
   - Build Command: `cd backend && mvn clean package -DskipTests`
   - Start Command: `cd backend && java -jar target/backend-1.0.0.jar`
5. Add Environment Variables:
   ```
   SPRING_PROFILES_ACTIVE=prod
   SPRING_DATASOURCE_URL=[Your Render PostgreSQL URL]
   SPRING_DATASOURCE_USERNAME=[Your Database User]
   SPRING_DATASOURCE_PASSWORD=[Your Database Password]
   APP_FRONTEND_URL=https://your-frontend.vercel.app
   APP_JWT_SECRET=[Generate with: openssl rand -base64 32]
   APP_JWT_EXPIRATION_MS=86400000
   APP_STORAGE_UPLOAD_DIR=/tmp/uploads
   PORT=8080
   ```
6. Click "Deploy Web Service"
7. Save the backend URL: `https://your-backend.onrender.com`

## Step 4: Database Schema Migration

1. Once backend is deployed, connect to your PostgreSQL database using pgAdmin or similar tool
2. Run the schema migration:
```sql
-- Run the schema from: backend/src/main/resources/schema-postgresql.sql
```

3. Or use Render's database shell to run the migration

## Step 5: Frontend Deployment (Vercel)

1. Go to [Vercel Dashboard](https://vercel.com/dashboard)
2. Click "Add New" → "Project"
3. Import your GitHub repository
4. Configure:
   - Framework Preset: `Vite`
   - Root Directory: `frontend`
   - Build Command: `npm run build`
   - Output Directory: `dist`
5. Add Environment Variables:
   ```
   VITE_API_BASE_URL=https://your-backend.onrender.com/api
   ```
6. Click "Deploy"
7. Save the frontend URL: `https://your-frontend.vercel.app`

## Step 6: Update CORS Configuration

1. Go to your Render backend service
2. Update the `APP_FRONTEND_URL` environment variable with your actual Vercel URL
3. Redeploy the backend

## Step 7: Production Verification

Test the complete flow:

1. **Register User:**
```bash
curl -X POST https://your-backend.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"securePassword123","name":"Test User"}'
```

2. **Create Job:**
```bash
curl -X POST https://your-backend.onrender.com/api/jobs/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"title":"Software Engineer","company":"Tech Corp","description":"Java developer role","minExperienceYears":3,"skills":["Java","Spring","React"]}'
```

3. **Upload Resume:**
```bash
curl -X POST https://your-backend.onrender.com/api/resumes/upload/JOB_ID \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "files=@resume.pdf"
```

4. **Run Analysis:**
```bash
curl -X POST https://your-backend.onrender.com/api/analysis/run/RESUME_ID/JOB_ID \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

5. **Check Admin Dashboard:**
```bash
curl https://your-backend.onrender.com/api/admin/overview \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Step 8: Docker Alternative Deployment

If you prefer Docker deployment:

1. Build and run with Docker Compose:
```bash
docker-compose up -d --build
```

2. Access the application:
- Frontend: http://localhost
- Backend: http://localhost:8080
- Database: localhost:5432

## Environment Variables Reference

### Backend (Render)
- `SPRING_PROFILES_ACTIVE`: `prod`
- `SPRING_DATASOURCE_URL`: PostgreSQL connection string
- `SPRING_DATASOURCE_USERNAME`: Database user
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `APP_FRONTEND_URL`: Your Vercel frontend URL
- `APP_JWT_SECRET`: Generate with `openssl rand -base64 32`
- `APP_JWT_EXPIRATION_MS`: `86400000` (24 hours)
- `APP_STORAGE_UPLOAD_DIR`: `/tmp/uploads`
- `PORT`: `8080`

### Frontend (Vercel)
- `VITE_API_BASE_URL`: Your Render backend URL with `/api` suffix

## Troubleshooting

### Backend Issues
- Check Render logs for startup errors
- Verify database connection string format
- Ensure all environment variables are set
- Check CORS configuration matches frontend URL

### Frontend Issues
- Verify VITE_API_BASE_URL is correct
- Check Vercel deployment logs
- Ensure backend is accessible from frontend

### Database Issues
- Verify database is accessible from backend
- Check schema migration completed successfully
- Ensure database credentials are correct

## Security Notes

1. **Never commit secrets to git**
2. **Use strong JWT secrets** (minimum 32 characters)
3. **Enable HTTPS** (automatic on Vercel and Render)
4. **Rotate secrets regularly**
5. **Monitor logs for suspicious activity**
6. **Keep dependencies updated**

## Scaling Considerations

- **Backend**: Render automatically scales based on load
- **Database**: Consider connection pooling for high traffic
- **Frontend**: Vercel provides global CDN distribution
- **File Storage**: Consider using S3 or similar for production file storage

## Monitoring

- **Backend Health**: `https://your-backend.onrender.com/actuator/health`
- **Render Dashboard**: Monitor resource usage and logs
- **Vercel Dashboard**: Monitor frontend performance and errors

## Backup Strategy

- **Database**: Render provides automated backups
- **Code**: GitHub repository
- **Configuration**: Environment variables in platform dashboards

## Support

For deployment issues:
- Render Documentation: https://render.com/docs
- Vercel Documentation: https://vercel.com/docs
- Spring Boot Documentation: https://spring.io/projects/spring-boot