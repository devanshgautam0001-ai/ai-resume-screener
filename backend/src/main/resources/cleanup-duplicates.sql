-- Database Cleanup Script
-- This script removes duplicate data and adds unique constraints to prevent future duplicates

-- Remove duplicate skills (keep first occurrence, delete rest)
DELETE FROM resume_skill_mapping 
WHERE skill_id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY LOWER(name) ORDER BY id) as rn
        FROM skills
    ) t WHERE rn > 1
);

DELETE FROM job_skill_mapping 
WHERE skill_id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY LOWER(name) ORDER BY id) as rn
        FROM skills
    ) t WHERE rn > 1
);

DELETE FROM skills 
WHERE id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY LOWER(name) ORDER BY id) as rn
        FROM skills
    ) t WHERE rn > 1
);

-- Add unique constraint on skills name (case-insensitive)
CREATE UNIQUE INDEX IF NOT EXISTS idx_skills_name_unique ON skills (LOWER(name));

-- Remove duplicate jobs (same title and company by same user)
DELETE FROM analysis_results 
WHERE job_id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY created_by, LOWER(title), LOWER(company) ORDER BY id) as rn
        FROM jobs
    ) t WHERE rn > 1
);

DELETE FROM candidate_rankings 
WHERE job_id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY created_by, LOWER(title), LOWER(company) ORDER BY id) as rn
        FROM jobs
    ) t WHERE rn > 1
);

DELETE FROM resumes 
WHERE job_id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY created_by, LOWER(title), LOWER(company) ORDER BY id) as rn
        FROM jobs
    ) t WHERE rn > 1
);

DELETE FROM job_skill_mapping 
WHERE job_id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY created_by, LOWER(title), LOWER(company) ORDER BY id) as rn
        FROM jobs
    ) t WHERE rn > 1
);

DELETE FROM parsed_resume_data 
WHERE resume_id IN (
    SELECT id FROM resumes WHERE job_id IN (
        SELECT id FROM (
            SELECT id, ROW_NUMBER() OVER (PARTITION BY created_by, LOWER(title), LOWER(company) ORDER BY id) as rn
            FROM jobs
        ) t WHERE rn > 1
    )
);

DELETE FROM resume_skill_mapping 
WHERE resume_id IN (
    SELECT id FROM resumes WHERE job_id IN (
        SELECT id FROM (
            SELECT id, ROW_NUMBER() OVER (PARTITION BY created_by, LOWER(title), LOWER(company) ORDER BY id) as rn
            FROM jobs
        ) t WHERE rn > 1
    )
);

DELETE FROM jobs 
WHERE id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY created_by, LOWER(title), LOWER(company) ORDER BY id) as rn
        FROM jobs
    ) t WHERE rn > 1
);

-- Add unique constraint on jobs (created_by + title + company)
CREATE UNIQUE INDEX IF NOT EXISTS idx_jobs_user_title_company_unique 
ON jobs (created_by, LOWER(title), LOWER(company));

-- Remove duplicate users by email
DELETE FROM audit_logs 
WHERE user_id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY LOWER(email) ORDER BY id) as rn
        FROM users
    ) t WHERE rn > 1
);

DELETE FROM jobs 
WHERE created_by IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY LOWER(email) ORDER BY id) as rn
        FROM users
    ) t WHERE rn > 1
);

DELETE FROM resumes 
WHERE uploaded_by IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY LOWER(email) ORDER BY id) as rn
        FROM users
    ) t WHERE rn > 1
);

DELETE FROM users 
WHERE id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY LOWER(email) ORDER BY id) as rn
        FROM users
    ) t WHERE rn > 1
);

-- Note: users table already has unique constraint on email, but this ensures cleanup

-- Report cleanup results
DO $$
DECLARE
    skill_count INT;
    job_count INT;
    user_count INT;
BEGIN
    SELECT COUNT(*) INTO skill_count FROM skills;
    SELECT COUNT(*) INTO job_count FROM jobs;
    SELECT COUNT(*) INTO user_count FROM users;
    
    RAISE NOTICE 'Cleanup complete. Skills: %, Jobs: %, Users: %', skill_count, job_count, user_count;
END $$;