package com.resumeai.service;

import com.resumeai.dto.AnalysisDTO;
import com.resumeai.exception.BadRequestException;
import com.resumeai.exception.ResourceNotFoundException;
import com.resumeai.model.*;
import com.resumeai.repository.AnalysisResultRepository;
import com.resumeai.repository.CandidateRankingRepository;
import com.resumeai.repository.JobSkillMappingRepository;
import com.resumeai.repository.ResumeSkillMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiAnalysisService {

    private final ResumeService resumeService;
    private final JobService jobService;
    private final AnalysisResultRepository analysisResultRepository;
    private final ResumeSkillMappingRepository resumeSkillMappingRepository;
    private final JobSkillMappingRepository jobSkillMappingRepository;
    private final CandidateRankingRepository candidateRankingRepository;
    private final AuditLogService auditLogService;
    private final CurrentUserService currentUserService;
    private final RankingProcedureService rankingProcedureService;

    @Transactional
    public AnalysisDTO.AnalysisResponse analyze(Long resumeId, Long jobId) {
        Resume resume = resumeService.getById(resumeId);
        Job job = jobService.getById(jobId);

        if (!Objects.equals(resume.getJob().getId(), job.getId())) {
            throw new BadRequestException("Resume must be uploaded against the same job before analysis");
        }

        ParsedResumeData parsedData = resumeService.getParsedData(resumeId);
        AnalysisResult result = buildAnalysis(resume, parsedData, job);

        AnalysisResult saved = analysisResultRepository.findByResumeIdAndJobId(resumeId, jobId)
            .map(existing -> merge(existing, result))
            .orElseGet(() -> analysisResultRepository.save(result));

        resume.setStatus(Resume.ResumeStatus.ANALYZED);
        auditLogService.log(currentUserService.getCurrentUser(), "ANALYSIS_RESULT", saved.getId(), "ANALYSIS_COMPLETED",
            "Generated analysis for resume " + resumeId + " and job " + jobId);

        rankingProcedureService.refreshRankings(jobId);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<AnalysisDTO.ResultRow> getResultsForJob(Long jobId) {
        return analysisResultRepository.findByJobIdOrderByOverallScoreDesc(jobId).stream()
            .map(this::toResultRow)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AnalysisDTO.RankingResponse> getRankings(Long jobId) {
        List<CandidateRanking> rankings = jobId == null
            ? candidateRankingRepository.findAllByOrderByCreatedAtDesc()
            : candidateRankingRepository.findByJobIdOrderByRankPositionAsc(jobId);

        return rankings.stream()
            .map(ranking -> new AnalysisDTO.RankingResponse(
                ranking.getId(),
                ranking.getJob().getId(),
                ranking.getResume().getId(),
                ranking.getResume().getParsedData().getCandidateName(),
                ranking.getRankPosition(),
                ranking.getOverallScore(),
                ranking.getAtsScore(),
                ranking.getResume().getSkillMappings().stream().limit(6).map(mapping -> mapping.getSkill().getName()).toList()
            ))
            .toList();
    }

    @Transactional(readOnly = true)
    public AnalysisResult getAnalysisEntity(Long analysisId) {
        return analysisResultRepository.findById(analysisId)
            .orElseThrow(() -> new ResourceNotFoundException("Analysis not found"));
    }

    private AnalysisResult buildAnalysis(Resume resume, ParsedResumeData parsedData, Job job) {
        Set<String> resumeSkills = resumeSkillMappingRepository.findByResumeId(resume.getId()).stream()
            .map(mapping -> mapping.getSkill().getName().toLowerCase(Locale.ROOT))
            .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> requiredSkills = jobSkillMappingRepository.findByJobId(job.getId()).stream()
            .map(mapping -> mapping.getSkill().getName().toLowerCase(Locale.ROOT))
            .collect(Collectors.toCollection(LinkedHashSet::new));

        List<String> matchedSkills = requiredSkills.stream()
            .filter(resumeSkills::contains)
            .map(this::pretty)
            .toList();

        List<String> missingSkills = requiredSkills.stream()
            .filter(skill -> !resumeSkills.contains(skill))
            .map(this::pretty)
            .toList();

        int skillsScore = scoreSkills(requiredSkills, matchedSkills.size());
        int experienceScore = scoreExperience(parsedData.getExperienceYears(), job.getMinExperienceYears());
        int educationScore = scoreEducation(parsedData.getEducationSummary());
        int atsScore = scoreAts(parsedData, matchedSkills.size(), missingSkills.size());
        int overallScore = Math.min(100, (int) Math.round(skillsScore * 0.40 + experienceScore * 0.35 + educationScore * 0.15 + atsScore * 0.10));

        List<String> suggestions = buildSuggestions(missingSkills, parsedData);
        String summary = buildSummary(job, parsedData, matchedSkills, missingSkills, overallScore);

        return AnalysisResult.builder()
            .resume(resume)
            .job(job)
            .matchScore(overallScore)
            .overallScore(overallScore)
            .skillsScore(skillsScore)
            .experienceScore(experienceScore)
            .educationScore(educationScore)
            .atsScore(atsScore)
            .missingSkillsText(String.join("\n", missingSkills))
            .improvementSuggestionsText(String.join("\n", suggestions))
            .summary(summary)
            .build();
    }

    private AnalysisResult merge(AnalysisResult existing, AnalysisResult updated) {
        existing.setMatchScore(updated.getMatchScore());
        existing.setOverallScore(updated.getOverallScore());
        existing.setSkillsScore(updated.getSkillsScore());
        existing.setExperienceScore(updated.getExperienceScore());
        existing.setEducationScore(updated.getEducationScore());
        existing.setAtsScore(updated.getAtsScore());
        existing.setMissingSkillsText(updated.getMissingSkillsText());
        existing.setImprovementSuggestionsText(updated.getImprovementSuggestionsText());
        existing.setSummary(updated.getSummary());
        return analysisResultRepository.save(existing);
    }

    private int scoreSkills(Set<String> requiredSkills, int matchedCount) {
        if (requiredSkills.isEmpty()) {
            return 70;
        }
        return Math.min(100, Math.round((matchedCount * 100f) / requiredSkills.size()));
    }

    private int scoreExperience(Integer candidateYears, Integer requiredYears) {
        int candidate = candidateYears == null ? 0 : candidateYears;
        int required = requiredYears == null ? 0 : requiredYears;
        if (required == 0) {
            return Math.min(100, 60 + candidate * 8);
        }
        if (candidate >= required) {
            return Math.min(100, 70 + (candidate - required) * 5);
        }
        return Math.max(35, 65 - ((required - candidate) * 12));
    }

    private int scoreEducation(String educationSummary) {
        if (educationSummary == null || educationSummary.isBlank()) {
            return 45;
        }
        String lower = educationSummary.toLowerCase(Locale.ROOT);
        if (lower.contains("b.tech") || lower.contains("bachelor") || lower.contains("master") || lower.contains("m.tech")) {
            return 85;
        }
        return 65;
    }

    private int scoreAts(ParsedResumeData parsedData, int matchedSkills, int missingSkills) {
        int completeness = (parsedData.getCandidateName() == null || parsedData.getCandidateName().isBlank() ? 0 : 15)
            + (parsedData.getEmail() == null || parsedData.getEmail().isBlank() ? 0 : 15)
            + (parsedData.getPhone() == null || parsedData.getPhone().isBlank() ? 0 : 10)
            + (parsedData.getProjectsSummary() == null || parsedData.getProjectsSummary().isBlank() ? 0 : 10)
            + (parsedData.getExperienceSummary() == null || parsedData.getExperienceSummary().isBlank() ? 0 : 10);
        int keywordCoverage = Math.max(20, Math.min(40, matchedSkills * 8));
        int penalty = Math.min(20, missingSkills * 3);
        return Math.max(40, Math.min(100, completeness + keywordCoverage + 20 - penalty));
    }

    private List<String> buildSuggestions(List<String> missingSkills, ParsedResumeData parsedData) {
        List<String> suggestions = new ArrayList<>();
        if (!missingSkills.isEmpty()) {
            suggestions.add("Add evidence of these missing skills where you have real experience: " + String.join(", ", missingSkills));
        }
        if (parsedData.getProjectsSummary() == null || parsedData.getProjectsSummary().isBlank()) {
            suggestions.add("Include a projects section with measurable results and the exact technologies used.");
        }
        suggestions.add("Rewrite the professional summary to align with the target role and include the strongest matching stack.");
        suggestions.add("Use quantified impact statements for experience entries to improve ATS and recruiter confidence.");
        return suggestions;
    }

    private String buildSummary(Job job, ParsedResumeData parsedData, List<String> matchedSkills, List<String> missingSkills, int overallScore) {
        return parsedData.getCandidateName() + " achieved an overall match score of " + overallScore
            + " for the " + job.getTitle() + " role. Matched skills: "
            + (matchedSkills.isEmpty() ? "none identified" : String.join(", ", matchedSkills))
            + ". Missing skills: " + (missingSkills.isEmpty() ? "none" : String.join(", ", missingSkills)) + ".";
    }

    private String pretty(String value) {
        return value.isBlank() ? value : Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private AnalysisDTO.AnalysisResponse toDto(AnalysisResult result) {
        Set<String> requiredSkills = jobSkillMappingRepository.findByJobId(result.getJob().getId()).stream()
            .map(mapping -> mapping.getSkill().getName().toLowerCase(Locale.ROOT))
            .collect(Collectors.toSet());

        List<String> matchedSkills = resumeSkillMappingRepository.findByResumeId(result.getResume().getId()).stream()
            .map(mapping -> mapping.getSkill().getName())
            .filter(skill -> requiredSkills.contains(skill.toLowerCase(Locale.ROOT)))
            .toList();

        return new AnalysisDTO.AnalysisResponse(
            result.getId(),
            result.getResume().getId(),
            result.getJob().getId(),
            result.getMatchScore(),
            result.getOverallScore(),
            result.getSkillsScore(),
            result.getExperienceScore(),
            result.getEducationScore(),
            result.getAtsScore(),
            matchedSkills,
            splitText(result.getMissingSkillsText()),
            splitText(result.getImprovementSuggestionsText()),
            result.getSummary(),
            result.getCreatedAt()
        );
    }

    private AnalysisDTO.ResultRow toResultRow(AnalysisResult result) {
        ParsedResumeData parsedData = result.getResume().getParsedData();
        return new AnalysisDTO.ResultRow(
            result.getId(),
            result.getResume().getId(),
            parsedData.getCandidateName(),
            parsedData.getEmail(),
            result.getOverallScore(),
            result.getAtsScore(),
            splitText(result.getMissingSkillsText()),
            result.getSummary()
        );
    }

    private List<String> splitText(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split("\\n"));
    }
}
