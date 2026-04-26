# AI Resume Screening System

Production-style Java + DBMS major project built on Spring Boot, PostgreSQL, React, Tailwind CSS, Apache Tika, JWT authentication, PDF export, and candidate ranking.

## 🚀 Quick Start

### Local Development
```bash
# Backend
cd backend
mvn spring-boot:run

# Frontend
cd frontend
npm install
npm run dev
```

## 🌐 Production Deployment

### Quick Production Setup
For complete production deployment instructions, see [PRODUCTION_DEPLOYMENT.md](./PRODUCTION_DEPLOYMENT.md)

### Production Deployment Options

#### Option 1: Vercel + Render (Recommended)
- **Frontend**: Deploy to Vercel
- **Backend**: Deploy to Render with PostgreSQL
- **Cost**: Free tier available for both
- **Setup**: See [PRODUCTION_DEPLOYMENT.md](./PRODUCTION_DEPLOYMENT.md)

#### Option 2: Docker Deployment
- **Full Stack**: Deploy using Docker Compose
- **Infrastructure**: Any VPS or cloud provider
- **Setup**: `docker-compose up -d --build`

#### Option 3: Manual Deployment
- **Backend**: VPS or cloud instance
- **Frontend**: Any web server with nginx
- **Database**: Managed PostgreSQL service

## Production Flow

1. Login or register a recruiter account.
2. Create a job description first.
3. Save the job in PostgreSQL.
4. Upload one or many resumes linked to the selected `job_id`.
5. Parse the resumes with Apache Tika.
6. Extract skills, experience, education, and projects.
7. Run AI screening for a selected candidate.
8. Persist match score, ATS score, missing skills, suggestions, and ranking data.
9. Review results, analytics, and candidate leaderboard.
10. Export a PDF report.

## Tech Stack

- Backend: Java 17, Spring Boot 3, Spring Security, Spring Data JPA, PostgreSQL, Apache Tika, PDFBox
- Frontend: React 18, Vite, Tailwind CSS, Recharts, Zustand, Axios
- Deployment: Docker Compose, Nginx, PostgreSQL

## Folder Structure

```text
ai-resume-screener/
├── backend/
├── frontend/
├── dbms-design.md
├── Dockerfile.backend
├── Dockerfile.frontend
├── docker-compose.yml
├── .env.example
├── postman_collection.json
└── README.md
```

## Database Schema

Core relational tables:

- `users`
- `jobs`
- `resumes`
- `parsed_resume_data`
- `skills`
- `resume_skill_mapping`
- `job_skill_mapping`
- `analysis_results`
- `candidate_rankings`
- `audit_logs`

DBMS notes:

- ER diagram and normalization notes are documented in [dbms-design.md](./dbms-design.md)
- PostgreSQL stored procedure: `refresh_candidate_rankings(job_id)`
- PostgreSQL trigger: `trg_analysis_audit`
- Schema bootstrap file: [backend/src/main/resources/schema-postgresql.sql](./backend/src/main/resources/schema-postgresql.sql)

## Required APIs

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/jobs/create`
- `GET /api/jobs`
- `POST /api/resumes/upload/{jobId}`
- `GET /api/resumes/job/{jobId}`
- `POST /api/analysis/run/{resumeId}/{jobId}`
- `GET /api/results/{jobId}`
- `GET /api/rankings?jobId={jobId}`
- `GET /api/export/pdf/{id}`

## Local Setup

### 1. PostgreSQL

Create a PostgreSQL database:

```sql
CREATE DATABASE resume_ai;
```

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

Environment variables:

- `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/resume_ai`
- `SPRING_DATASOURCE_USERNAME=postgres`
- `SPRING_DATASOURCE_PASSWORD=postgres`
- `APP_FRONTEND_URL=http://localhost:5173`
- `APP_STORAGE_UPLOAD_DIR=/tmp/uploads`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Local frontend API target:

- `VITE_API_BASE_URL=http://localhost:8081/api` for the current local machine setup
- `VITE_API_BASE_URL=http://localhost:8080/api` for standard backend runs

## Docker Deployment

Docker files are ready, but Docker is not installed on this machine right now, so container startup could not be verified here.

When Docker is available, run:

```bash
docker compose up --build
```

Services:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080/api`
- PostgreSQL: `localhost:5432`

## Build Verification

Verified locally in this workspace:

- Backend package build: `mvn package -DskipTests`
- Backend tests: `mvn "-Dmaven.repo.local=.m2" test`
- Frontend production build: `npm run build`

## Sample API Response

### `POST /api/analysis/run/{resumeId}/{jobId}`

```json
{
  "success": true,
  "message": "Analysis completed successfully",
  "data": {
    "id": 7,
    "resumeId": 5,
    "jobId": 2,
    "matchScore": 86,
    "overallScore": 84,
    "skillsScore": 86,
    "experienceScore": 78,
    "educationScore": 85,
    "atsScore": 82,
    "matchedSkills": ["Java", "Spring boot", "React"],
    "missingSkills": ["Docker", "Jwt"],
    "optimizationSuggestions": [
      "Add evidence of these missing skills where you have real experience: Docker, Jwt",
      "Rewrite the professional summary to align with the target role and include the strongest matching stack."
    ],
    "summary": "Candidate achieved an overall match score..."
  }
}
```

## UI Preview

- Dashboard: step-based SaaS workflow with live metrics and ranking chart
- Create Job: structured recruiter form with skills preview
- Upload Resumes: job-linked multi-file upload and parsing feedback
- Results Analytics: live score meter, ATS breakdown, radar chart, stored result table, and leaderboard
- Admin Panel: platform-level counts for users, jobs, resumes, and analyses

## Deployment Options

### Platform-Specific Deployment

#### Option 1: Vercel + Render (Recommended)
- **Frontend**: Deploy to Vercel
- **Backend**: Deploy to Render with PostgreSQL
- **Cost**: Free tier available for both
- **Setup**: See [DEPLOYMENT.md](./DEPLOYMENT.md#vercel--render-deployment)

#### Option 2: Docker Deployment
- **Full Stack**: Deploy using Docker Compose
- **Infrastructure**: Any VPS or cloud provider
- **Setup**: See [DEPLOYMENT.md](./DEPLOYMENT.md#docker-deployment)

#### Option 3: Manual Deployment
- **Backend**: VPS or cloud instance
- **Frontend**: Any web server with nginx
- **Database**: Managed PostgreSQL service
- **Setup**: See [DEPLOYMENT.md](./DEPLOYMENT.md#manual-deployment)

### Environment Configuration

Copy `.env.example` to `.env` and configure:
```bash
# Backend
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://your-host:5432/resume_ai
APP_FRONTEND_URL=https://your-frontend-domain.com
APP_JWT_SECRET=your_secure_secret

# Frontend
VITE_API_BASE_URL=https://your-backend-domain.com/api
```

### Production Build

#### Backend
```bash
cd backend
mvn clean package -DskipTests
```

#### Frontend
```bash
cd frontend
npm run build
```

### Docker Deployment
```bash
docker-compose up -d
```

## Deployment Notes

- **Render**:
  - Deploy PostgreSQL as a managed database
  - Deploy backend as a Docker web service
  - Deploy frontend as a static site or Docker service
- **Railway**:
  - Use Docker Compose split into separate services
  - Set environment variables from `.env.example`
- **AWS**:
  - Backend on ECS/App Runner
  - Frontend on S3 + CloudFront or ECS
  - PostgreSQL on RDS

## Important Scope Note

This project is now refactored into a real PostgreSQL-backed, end-to-end deployable model. Public internet deployment was not completed from this session because it requires your cloud account, credentials, and a live Docker or hosting environment.
