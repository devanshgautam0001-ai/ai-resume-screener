#!/bin/bash

# Database Initialization Script for Production
# This script initializes the database schema for production deployment

set -e

# Database configuration from environment variables
DB_HOST="${SPRING_DATASOURCE_URL:-jdbc:postgresql://localhost:5432/resume_ai}"
DB_NAME="${DB_NAME:-resume_ai}"
DB_USER="${SPRING_DATASOURCE_USERNAME:-postgres}"
DB_PASSWORD="${SPRING_DATASOURCE_PASSWORD:-postgres}"

# Extract host from JDBC URL
DB_HOST=$(echo $DB_HOST | sed -n 's/.*:\/\/\([^:]*\):.*/\1/p')
DB_PORT=$(echo $DB_URL | sed -n 's/.*:\([0-9]*\)\/.*/\1/p')

echo "Initializing database..."
echo "Host: $DB_HOST"
echo "Database: $DB_NAME"
echo "User: $DB_USER"

# Check if PostgreSQL client is installed
if ! command -v psql &> /dev/null; then
    echo "Error: PostgreSQL client (psql) is not installed"
    echo "Install it using: apt-get install postgresql-client"
    exit 1
fi

# Set PGPASSWORD environment variable for non-interactive authentication
export PGPASSWORD=$DB_PASSWORD

# Check database connection
echo "Testing database connection..."
if ! psql -h "$DB_HOST" -p "${DB_PORT:-5432}" -U "$DB_USER" -d "$DB_NAME" -c '\q' 2>/dev/null; then
    echo "Error: Cannot connect to database"
    echo "Please check your connection parameters and ensure the database exists"
    exit 1
fi

echo "Connection successful!"

# Run schema initialization
echo "Applying database schema..."
psql -h "$DB_HOST" -p "${DB_PORT:-5432}" -U "$DB_USER" -d "$DB_NAME" -f backend/src/main/resources/schema-postgresql.sql

if [ $? -eq 0 ]; then
    echo "✅ Database schema initialized successfully!"
else
    echo "❌ Schema initialization failed"
    exit 1
fi

# Verify tables were created
echo "Verifying table creation..."
TABLE_COUNT=$(psql -h "$DB_HOST" -p "${DB_PORT:-5432}" -U "$DB_USER" -d "$DB_NAME" -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'")

if [ "$TABLE_COUNT" -gt 0 ]; then
    echo "✅ $TABLE_COUNT tables created successfully"
else
    echo "⚠️  Warning: No tables found in database"
fi

echo "Database initialization complete!"

unset PGPASSWORD