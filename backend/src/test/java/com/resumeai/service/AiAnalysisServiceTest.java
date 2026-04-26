package com.resumeai.service;

import com.resumeai.dto.AnalysisDTO;
import com.resumeai.model.*;
import com.resumeai.repository.AnalysisResultRepository;
import com.resumeai.repository.CandidateRankingRepository;
import com.resumeai.repository.JobSkillMappingRepository;
import com.resumeai.repository.ResumeSkillMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AiAnalysisServiceTest {

    private ResumeService resumeService;
    private JobService jobService;
    private AnalysisResultRepository analysisResultRepository;
    private ResumeSkillMappingRepository resumeSkillMappingRepository;
    private JobSkillMappingRepository jobSkillMappingRepository;
    private CandidateRankingRepository candidateRankingRepository;
    private AuditLogService auditLogService;
    private CurrentUserService currentUserService;
    private RankingProcedureService rankingProcedureService;
    private AiAnalysisService aiAnalysisService;

    @BeforeEach
    void setUp() {
        resumeService = mock(ResumeService.class);
        jobService = mock(JobService.class);
        analysisResultRepository = mock(AnalysisResultRepository.class);
        resumeSkillMappingRepository = mock(ResumeSkillMappingRepository.class);
        jobSkillMappingRepository = mock(JobSkillMappingRepository.class);
        candidateRankingRepository = mock(CandidateRankingRepository.class);
        auditLogService = mock(AuditLogService.class);
        currentUserService = mock(CurrentUserService.class);
        rankingProcedureService = mock(RankingProcedureService.class);

        aiAnalysisService = new AiAnalysisService(
            resumeService,
            jobService,
            analysisResultRepository,
            resumeSkillMappingRepository,
            jobSkillMappingRepository,
            candidateRankingRepository,
            auditLogService,
            currentUserService,
            rankingProcedureService
        );
    }

    @Test
    void shouldGenerateHighScoreForStrongSkillOverlap() {
        User recruiter = User.builder().id(10L).email("recruiter@test.com").name("Recruiter").password("x").role(User.Role.ROLE_RECRUITER).build();
        Job job = Job.builder()
            .id(2L)
            .createdBy(recruiter)
            .title("Full Stack Engineer")
            .company("NovaAI")
            .description("Need Java Spring Boot React experience")
            .minExperienceYears(1)
            .build();

        Resume resume = Resume.builder()
            .id(1L)
            .job(job)
            .uploadedBy(recruiter)
            .originalFileName("resume.pdf")
            .storedFileName("resume.pdf")
            .contentType("application/pdf")
            .fileSize(100L)
            .storagePath("uploads/resume.pdf")
            .status(Resume.ResumeStatus.PARSED)
            .build();

        ParsedResumeData parsedResumeData = ParsedResumeData.builder()
            .resume(resume)
            .candidateName("Aarav Sharma")
            .email("aarav@example.com")
            .experienceYears(2)
            .educationSummary("B.Tech Computer Science")
            .experienceSummary("Built microservices")
            .projectsSummary("Resume screener")
            .build();
        resume.setParsedData(parsedResumeData);

        Skill java = Skill.builder().id(1L).name("java").build();
        Skill spring = Skill.builder().id(2L).name("spring boot").build();
        Skill react = Skill.builder().id(3L).name("react").build();

        when(resumeService.getById(1L)).thenReturn(resume);
        when(resumeService.getParsedData(1L)).thenReturn(parsedResumeData);
        when(jobService.getById(2L)).thenReturn(job);
        when(currentUserService.getCurrentUser()).thenReturn(recruiter);
        when(resumeSkillMappingRepository.findByResumeId(1L)).thenReturn(List.of(
            ResumeSkillMapping.builder().resume(resume).skill(java).build(),
            ResumeSkillMapping.builder().resume(resume).skill(spring).build(),
            ResumeSkillMapping.builder().resume(resume).skill(react).build()
        ));
        when(jobSkillMappingRepository.findByJobId(2L)).thenReturn(List.of(
            JobSkillMapping.builder().job(job).skill(java).build(),
            JobSkillMapping.builder().job(job).skill(spring).build(),
            JobSkillMapping.builder().job(job).skill(react).build()
        ));
        when(analysisResultRepository.findByResumeIdAndJobId(1L, 2L)).thenReturn(Optional.empty());
        when(analysisResultRepository.save(any())).thenAnswer(invocation -> {
            AnalysisResult result = invocation.getArgument(0);
            result.setId(99L);
            return result;
        });

        AnalysisDTO.AnalysisResponse response = aiAnalysisService.analyze(1L, 2L);

        assertThat(response.overallScore()).isGreaterThanOrEqualTo(80);
        assertThat(response.missingSkills()).isEmpty();
        verify(rankingProcedureService).refreshRankings(2L);
    }
}
