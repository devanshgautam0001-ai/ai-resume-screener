-- Optional sample seed for viva/demo environments only.
-- Run after registering at least one recruiter user or replace created_by/uploaded_by values.

INSERT INTO jobs (created_by, title, company, location, employment_type, min_experience_years, description, status)
VALUES
  (1, 'Senior Full Stack AI Engineer', 'ResumeAI Labs', 'Bengaluru', 'Full-time', 2,
   'Need Java, Spring Boot, React, PostgreSQL, Docker, JWT, and NLP experience for an AI hiring product.', 'ACTIVE');

INSERT INTO skills (name, category) VALUES
  ('java', 'GENERAL'),
  ('spring boot', 'GENERAL'),
  ('react', 'GENERAL'),
  ('postgresql', 'GENERAL'),
  ('docker', 'GENERAL'),
  ('jwt', 'GENERAL'),
  ('nlp', 'GENERAL')
ON CONFLICT (name) DO NOTHING;

INSERT INTO job_skill_mapping (job_id, skill_id, required)
SELECT 1, s.id, TRUE
FROM skills s
WHERE s.name IN ('java', 'spring boot', 'react', 'postgresql', 'docker', 'jwt', 'nlp')
ON CONFLICT DO NOTHING;
