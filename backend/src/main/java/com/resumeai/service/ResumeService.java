package com.resumeai.service;

import com.resumeai.dto.ResumeDTO;
import com.resumeai.exception.BadRequestException;
import com.resumeai.exception.ResourceNotFoundException;
import com.resumeai.model.*;
import com.resumeai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ParsedResumeDataRepository parsedResumeDataRepository;
    private final ResumeSkillMappingRepository resumeSkillMappingRepository;
    private final SkillRepository skillRepository;
    private final ResumeParserService resumeParserService;
    private final JobService jobService;
    private final CurrentUserService currentUserService;
    private final AuditLogService auditLogService;

    @Value("${app.storage.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public ResumeDTO.UploadResponse uploadResumes(Long jobId, MultipartFile[] files) throws Exception {
        if (files == null || files.length == 0) {
            throw new BadRequestException("At least one resume file is required");
        }

        Job job = jobService.getByIdAndValidateOwnership(jobId);
        User currentUser = currentUserService.getCurrentUser();
        List<ResumeDTO.ResumeResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            responses.add(uploadSingle(job, currentUser, file));
        }

        return new ResumeDTO.UploadResponse(jobId, responses, "Resumes uploaded and parsed successfully");
    }

    public Resume getById(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        return resumeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
    }

    public ResumeDTO.ResumeResponse findById(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        Resume resume = resumeRepository.findByIdAndUploadedById(id, currentUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Resume not found or you don't have access"));
        return toDto(resume);
    }

    public ParsedResumeData getParsedData(Long resumeId) {
        return parsedResumeDataRepository.findByResumeId(resumeId)
            .orElseThrow(() -> new ResourceNotFoundException("Parsed resume data not found"));
    }

    @Transactional(readOnly = true)
    public List<ResumeDTO.ResumeResponse> findByJobId(Long jobId) {
        User currentUser = currentUserService.getCurrentUser();
        // First verify the user owns the job
        Job job = jobService.getByIdAndValidateOwnership(jobId);
        return resumeRepository.findByJobIdAndUploadedByIdOrderByCreatedAtDesc(jobId, currentUser.getId()).stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ResumeDTO.ResumeResponse> findAll() {
        User currentUser = currentUserService.getCurrentUser();
        return resumeRepository.findByUploadedByIdOrderByCreatedAtDesc(currentUser.getId()).stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional
    protected ResumeDTO.ResumeResponse uploadSingle(Job job, User currentUser, MultipartFile file) throws Exception {
        validateFile(file);
        Path storedPath = storeFile(job.getId(), file);
        ResumeParserService.ParsedResumeDraft parsedDraft = resumeParserService.parse(file);

        Resume resume = Resume.builder()
            .job(job)
            .uploadedBy(currentUser)
            .originalFileName(file.getOriginalFilename())
            .storedFileName(storedPath.getFileName().toString())
            .contentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType())
            .fileSize(file.getSize())
            .storagePath(storedPath.toString())
            .status(Resume.ResumeStatus.PARSED)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        Resume savedResume = resumeRepository.save(resume);

        ParsedResumeData parsedResumeData = ParsedResumeData.builder()
            .resume(savedResume)
            .candidateName(parsedDraft.candidateName())
            .email(parsedDraft.email())
            .phone(parsedDraft.phone())
            .experienceYears(parsedDraft.experienceYears())
            .experienceSummary(String.join("\n", parsedDraft.experiences()))
            .educationSummary(String.join("\n", parsedDraft.education()))
            .projectsSummary(String.join("\n", parsedDraft.projects()))
            .rawText(parsedDraft.rawText())
            .build();
        parsedResumeDataRepository.save(parsedResumeData);

        resumeSkillMappingRepository.deleteByResumeId(savedResume.getId());
        for (String skillName : parsedDraft.skills()) {
            Skill skill = skillRepository.findFirstByNameIgnoreCase(skillName)
                .orElseGet(() -> skillRepository.save(Skill.builder().name(skillName.toLowerCase(Locale.ROOT)).category("RESUME").build()));

            resumeSkillMappingRepository.save(ResumeSkillMapping.builder()
                .resume(savedResume)
                .skill(skill)
                .confidenceScore(100)
                .build());
        }

        auditLogService.log(currentUser, "RESUME", savedResume.getId(), "RESUME_UPLOADED",
            "Uploaded resume " + file.getOriginalFilename() + " for job " + job.getId());

        return toDto(savedResume);
    }

    private void validateFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        if (file.isEmpty()) {
            throw new BadRequestException("Resume file cannot be empty");
        }
        if (!(originalFileName.endsWith(".pdf") || originalFileName.endsWith(".docx"))) {
            throw new BadRequestException("Only PDF and DOCX resumes are supported");
        }
    }

    private Path storeFile(Long jobId, MultipartFile file) throws Exception {
        Path jobDirectory = Path.of(uploadDir, "job-" + jobId);
        Files.createDirectories(jobDirectory);

        String safeName = (file.getOriginalFilename() == null ? "resume" : file.getOriginalFilename()).replaceAll("[^a-zA-Z0-9._-]", "_");
        Path destination = jobDirectory.resolve(UUID.randomUUID() + "-" + safeName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toAbsolutePath();
    }

    private ResumeDTO.ResumeResponse toDto(Resume resume) {
        ParsedResumeData parsedData = resume.getParsedData() != null ? resume.getParsedData() : getParsedData(resume.getId());
        List<String> skills = resume.getSkillMappings().stream()
            .map(mapping -> mapping.getSkill().getName())
            .toList();

        return new ResumeDTO.ResumeResponse(
            resume.getId(),
            resume.getJob().getId(),
            parsedData.getCandidateName(),
            parsedData.getEmail(),
            parsedData.getPhone(),
            resume.getOriginalFileName(),
            resume.getStatus().name(),
            skills,
            splitSummary(parsedData.getExperienceSummary()),
            splitSummary(parsedData.getEducationSummary()),
            splitSummary(parsedData.getProjectsSummary()),
            resume.getCreatedAt()
        );
    }

    private List<String> splitSummary(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split("\\n"));
    }
}
