package com.resumeai.service;

import com.resumeai.dto.JobDTO;
import com.resumeai.exception.ResourceNotFoundException;
import com.resumeai.model.Job;
import com.resumeai.model.JobSkillMapping;
import com.resumeai.model.Skill;
import com.resumeai.model.User;
import com.resumeai.repository.JobRepository;
import com.resumeai.repository.JobSkillMappingRepository;
import com.resumeai.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobSkillMappingRepository jobSkillMappingRepository;
    private final SkillRepository skillRepository;
    private final CurrentUserService currentUserService;
    private final AuditLogService auditLogService;

    @Transactional
    public JobDTO.JobResponse create(JobDTO.CreateJobRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Job job = Job.builder()
            .createdBy(currentUser)
            .title(request.title().trim())
            .company(request.company().trim())
            .location(trimToNull(request.location()))
            .employmentType(trimToNull(request.employmentType()))
            .minExperienceYears(request.minExperienceYears())
            .description(request.description().trim())
            .status("ACTIVE")
            .build();

        Job savedJob = jobRepository.save(job);
        syncRequiredSkills(savedJob, request.requiredSkills(), request.description());
        auditLogService.log(currentUser, "JOB", savedJob.getId(), "JOB_CREATED", "Created job " + savedJob.getTitle());

        return toDto(jobRepository.findById(savedJob.getId()).orElseThrow());
    }

    public Job getById(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        return jobRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }

    public Job getByIdAndValidateOwnership(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        return jobRepository.findByIdAndCreatedById(id, currentUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Job not found or you don't have access"));
    }

    @Transactional(readOnly = true)
    public List<JobDTO.JobResponse> getAll() {
        User currentUser = currentUserService.getCurrentUser();
        return jobRepository.findAllByCreatedByIdOrderByCreatedAtDesc(currentUser.getId()).stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<JobDTO.JobResponse> getCurrentUserJobs() {
        User currentUser = currentUserService.getCurrentUser();
        return jobRepository.findAllByCreatedByIdOrderByCreatedAtDesc(currentUser.getId()).stream()
            .map(this::toDto)
            .toList();
    }

    private void syncRequiredSkills(Job job, List<String> requiredSkills, String description) {
        List<String> normalized = requiredSkills == null || requiredSkills.isEmpty()
            ? deriveKeywords(description)
            : requiredSkills.stream().map(this::normalizeSkill).filter(value -> !value.isBlank()).distinct().toList();

        jobSkillMappingRepository.deleteByJobId(job.getId());

        List<JobSkillMapping> mappings = new ArrayList<>();
        for (String skillName : normalized) {
            Skill skill = skillRepository.findFirstByNameIgnoreCase(skillName)
                .orElseGet(() -> skillRepository.save(Skill.builder().name(skillName).category("GENERAL").build()));

            mappings.add(JobSkillMapping.builder()
                .job(job)
                .skill(skill)
                .required(true)
                .build());
        }
        jobSkillMappingRepository.saveAll(mappings);
    }

    private List<String> deriveKeywords(String description) {
        return List.of(description.split("[,\\n]"))
            .stream()
            .map(this::normalizeSkill)
            .filter(value -> value.length() > 2)
            .distinct()
            .limit(15)
            .toList();
    }

    private String normalizeSkill(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public JobDTO.JobResponse toDto(Job job) {
        List<String> skills = job.getRequiredSkills().stream()
            .map(mapping -> mapping.getSkill().getName())
            .toList();

        return new JobDTO.JobResponse(
            job.getId(),
            job.getTitle(),
            job.getCompany(),
            job.getLocation(),
            job.getEmploymentType(),
            job.getMinExperienceYears(),
            job.getDescription(),
            job.getStatus(),
            skills,
            job.getCreatedAt()
        );
    }
}
