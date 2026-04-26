package com.resumeai.dto;

import java.time.Instant;
import java.util.List;

public class AnalysisDTO {

    public record AnalysisResponse(
        Long id,
        Long resumeId,
        Long jobId,
        Integer matchScore,
        Integer overallScore,
        Integer skillsScore,
        Integer experienceScore,
        Integer educationScore,
        Integer atsScore,
        List<String> matchedSkills,
        List<String> missingSkills,
        List<String> optimizationSuggestions,
        String summary,
        Instant createdAt
    ) {
    }

    public record ResultRow(
        Long analysisId,
        Long resumeId,
        String candidateName,
        String email,
        Integer overallScore,
        Integer atsScore,
        List<String> missingSkills,
        String summary
    ) {
    }

    public record RankingResponse(
        Long rankingId,
        Long jobId,
        Long resumeId,
        String candidateName,
        Integer rankPosition,
        Integer overallScore,
        Integer atsScore,
        List<String> topSkills
    ) {
    }
}
