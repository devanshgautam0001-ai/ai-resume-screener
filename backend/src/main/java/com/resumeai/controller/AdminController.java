package com.resumeai.controller;

import com.resumeai.dto.ApiResponse;
import com.resumeai.model.AnalysisResult;
import com.resumeai.model.CandidateRanking;
import com.resumeai.model.Job;
import com.resumeai.model.ParsedResumeData;
import com.resumeai.repository.AnalysisResultRepository;
import com.resumeai.repository.CandidateRankingRepository;
import com.resumeai.repository.JobRepository;
import com.resumeai.repository.ParsedResumeDataRepository;
import com.resumeai.repository.ResumeRepository;
import com.resumeai.repository.UserRepository;
import com.resumeai.service.DatabaseCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final CandidateRankingRepository candidateRankingRepository;
    private final ParsedResumeDataRepository parsedResumeDataRepository;
    private final DatabaseCleanupService databaseCleanupService;

    @GetMapping("/overview")
    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> overview() {
        Map<String, Object> payload = new HashMap<>();
        
        // Basic counts
        long totalCandidates = parsedResumeDataRepository.count();
        long totalUsers = userRepository.count();
        long totalJobs = jobRepository.count();
        long totalResumes = resumeRepository.count();
        long totalAnalyses = analysisResultRepository.count();
        
        // Active jobs (status = ACTIVE)
        long activeJobs = jobRepository.findByStatus("ACTIVE").size();
        
        // Calculate average match score
        List<AnalysisResult> allAnalyses = analysisResultRepository.findAll();
        double avgScore = allAnalyses.stream()
            .mapToInt(AnalysisResult::getOverallScore)
            .average()
            .orElse(0.0);
        
        // Shortlisted count (candidates with score >= 80)
        long shortlistedCount = allAnalyses.stream()
            .filter(result -> result.getOverallScore() >= 80)
            .count();
        
        payload.put("totalCandidates", totalCandidates);
        payload.put("totalUsers", totalUsers);
        payload.put("totalJobs", totalJobs);
        payload.put("activeJobs", activeJobs);
        payload.put("totalResumes", totalResumes);
        payload.put("totalAnalyses", totalAnalyses);
        payload.put("averageMatchScore", Math.round(avgScore));
        payload.put("shortlistedCount", shortlistedCount);

        // Get top ranked candidates with proper fetching
        List<CandidateRanking> topRankings = candidateRankingRepository.findTop5ByOrderByOverallScoreDesc();
        payload.put("topCandidates", topRankings.stream()
            .map(ranking -> {
                ParsedResumeData parsedData = ranking.getResume().getParsedData();
                Job job = ranking.getJob();
                return Map.of(
                    "candidateName", parsedData != null ? parsedData.getCandidateName() : "Unknown",
                    "score", ranking.getOverallScore(),
                    "jobTitle", job != null ? job.getTitle() : "Unknown"
                );
            })
            .toList());

        return new ApiResponse<>(true, "Admin overview fetched", payload);
    }

    @PostMapping("/cleanup")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<String> cleanupDatabase() {
        String report = databaseCleanupService.cleanupDuplicates();
        return new ApiResponse<>(true, "Database cleanup completed successfully", report);
    }
}
