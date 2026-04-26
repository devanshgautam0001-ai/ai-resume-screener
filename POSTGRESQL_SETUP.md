# PostgreSQL Configuration Guide

This guide covers setting up PostgreSQL for the AI Resume Screening System using local, Render, or Neon PostgreSQL.

## Local PostgreSQL Setup

### 1. Install PostgreSQL

**Windows:**
- Download from: https://www.postgresql.org/download/windows/
- Run installer and note your password
- Default port: 5432

**Mac:**
```bash
brew install postgresql@16
brew services start postgresql@16
```

**Linux:**
```bash
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### 2. Create Database

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE resume_ai;

-- Create user (optional)
CREATE USER resume_ai_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE resume_ai TO resume_ai_user;

-- Exit
\q
```

### 3. Configure Application

Update `.env` file:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/resume_ai
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_postgres_password
DDL_AUTO=update
SPRING_PROFILES_ACTIVE=dev
```

### 4. Start Application

```bash
cd backend
mvn spring-boot:run
```

The application will:
- Connect to PostgreSQL automatically
- Create/update schema using Hibernate
- Seed initial skills data
- Be ready for use

## Render PostgreSQL Setup

### 1. Create Render Account

1. Go to [render.com](https://render.com)
2. Sign up and create a new account
3. Create a new PostgreSQL database

### 2. Get Database Connection Details

From your Render PostgreSQL dashboard:
- **Internal Database URL**: `postgresql://username:password@host:5432/database_name`
- **External Database URL**: Same format but accessible externally

### 3. Configure Environment Variables

In your Render web service, add these environment variables:

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://username:password@host:5432/database_name
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password
DDL_AUTO=validate
APP_FRONTEND_URL=https://your-frontend.vercel.app
APP_JWT_SECRET=your_generated_jwt_secret
APP_STORAGE_UPLOAD_DIR=/tmp/uploads
```

### 4. Deploy Backend

1. Connect your GitHub repository to Render
2. Configure build settings:
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/backend-1.0.0.jar`
3. Add environment variables
4. Deploy

### 5. Initialize Database Schema

For production deployment, you have two options:

**Option A: Let Hibernate Create Schema (First Time Only)**
1. Temporarily set `DDL_AUTO=update`
2. Deploy the application
3. Hibernate will create all tables
4. Change `DDL_AUTO=validate` for subsequent deployments
5. Redeploy

**Option B: Use SQL Schema**
1. Access your Render PostgreSQL database using the "Connect" button
2. Run the schema from `backend/src/main/resources/schema-postgresql.sql`
3. Keep `DDL_AUTO=validate` always

## Neon PostgreSQL Setup

### 1. Create Neon Account

1. Go to [neon.tech](https://neon.tech)
2. Sign up and create a new project
3. Choose PostgreSQL version 16

### 2. Get Connection String

From your Neon dashboard:
- Copy the connection string
- Format: `postgresql://username:password@ep-cool-region.aws.neon.tech/neondb?sslmode=require`

### 3. Configure Application

Update `.env` or deployment environment variables:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://username:password@ep-cool-region.aws.neon.tech/neondb?sslmode=require
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password
DDL_AUTO=validate
SPRING_PROFILES_ACTIVE=prod
```

### 4. Deploy to Your Platform

Use the connection string in your deployment platform (Render, Railway, AWS, etc.)

## Database Schema Management

### Development (Local)
- **DDL_AUTO**: `update`
- **Behavior**: Hibernate automatically creates/updates tables
- **Data**: Automatically seeded with skills

### Production
- **DDL_AUTO**: `validate`
- **Behavior**: Hibernate validates schema against entities
- **Data**: Manual seeding if needed

### Schema Initialization Without psql

The application includes automatic schema management:

1. **Hibernate DDL Auto**: Handles schema creation/updates
2. **DatabaseInitializer**: Seeds initial data (skills)
3. **No psql required**: Everything happens through JDBC

## Connection Pool Configuration

The application uses HikariCP with these settings:

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

Adjust these based on your expected load:
- **Low traffic**: `maximum-pool-size=5`
- **Medium traffic**: `maximum-pool-size=10` (default)
- **High traffic**: `maximum-pool-size=20`

## Troubleshooting

### Connection Issues

**Problem**: Cannot connect to database
```
Solution: 
1. Verify database is running
2. Check connection string format
3. Ensure firewall allows connections
4. Verify credentials
```

### Schema Issues

**Problem**: Tables don't exist
```
Solution:
1. Set DDL_AUTO=update temporarily
2. Restart application
3. Change back to DDL_AUTO=validate
```

### SSL Issues

**Problem**: SSL connection errors
```
Solution: Add ?sslmode=require to connection string
jdbc:postgresql://host:5432/db?sslmode=require
```

### Performance Issues

**Problem**: Slow database queries
```
Solution:
1. Check connection pool size
2. Add indexes to frequently queried columns
3. Enable query logging to identify slow queries
4. Consider read replicas for high read volume
```

## Backup and Recovery

### Local Backup
```bash
pg_dump -U postgres resume_ai > backup.sql
```

### Restore
```bash
psql -U postgres resume_ai < backup.sql
```

### Render/Neon Backup
Both platforms provide automated backups. Check their dashboards for:
- Automated backup schedules
- Point-in-time recovery
- Manual backup options

## Security Best Practices

1. **Never commit credentials** to version control
2. **Use strong passwords** for database users
3. **Enable SSL** for production connections
4. **Limit database user permissions** to only what's needed
5. **Regular backups** with tested restore procedures
6. **Monitor connection logs** for suspicious activity
7. **Use environment variables** for sensitive data

## Monitoring

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Database Metrics
Monitor:
- Connection pool usage
- Query performance
- Database size
- Active connections

### Logging
The application logs database operations in development mode:
```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## Migration Guide

### From H2/In-Memory to PostgreSQL

1. **Update dependencies** (H2 removal not needed - never existed)
2. **Update connection string** to PostgreSQL format
3. **Set DDL_AUTO=update** for first run
4. **Start application** - Hibernate will create schema
5. **Seed data** via DatabaseInitializer
6. **Set DDL_AUTO=validate** for production
7. **Test all functionality**

### Data Migration

If you have existing data:
1. Export data from old database
2. Transform to match new schema
3. Import to PostgreSQL
4. Validate data integrity
5. Update application to use new database