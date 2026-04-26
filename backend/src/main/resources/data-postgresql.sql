-- Seed Data for AI Resume Screening System
-- This script populates the database with sample data for testing and development

-- Insert sample skills
INSERT INTO skills (name, category) VALUES 
('Java', 'PROGRAMMING'),
('Spring Boot', 'FRAMEWORK'),
('React', 'FRAMEWORK'),
('JavaScript', 'PROGRAMMING'),
('Python', 'PROGRAMMING'),
('PostgreSQL', 'DATABASE'),
('Docker', 'DEVOPS'),
('Kubernetes', 'DEVOPS'),
('AWS', 'CLOUD'),
('Git', 'TOOLS'),
('Maven', 'TOOLS'),
('REST API', 'ARCHITECTURE'),
('Microservices', 'ARCHITECTURE'),
('TypeScript', 'PROGRAMMING'),
('Node.js', 'PROGRAMMING'),
('MongoDB', 'DATABASE'),
('Redis', 'DATABASE'),
('Linux', 'OS'),
('Agile', 'METHODOLOGY'),
('CI/CD', 'DEVOPS')
ON CONFLICT (name) DO NOTHING;

-- Note: Users, jobs, and resumes should be created through the application
-- to ensure proper password hashing and file handling
-- This seed file primarily sets up the skills reference data