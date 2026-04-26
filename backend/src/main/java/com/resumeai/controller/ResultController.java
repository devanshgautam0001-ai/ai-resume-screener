package com.resumeai.controller;

import com.resumeai.dto.AnalysisDTO;
import com.resumeai.dto.ApiResponse;
import com.resumeai.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResultController {

    private final AiAnalysisService aiAnalysisService;

    @GetMapping("/results/{jobId}")
    public ApiResponse<List<AnalysisDTO.ResultRow>> getResults(@PathVariable Long jobId) {
        return new ApiResponse<>(true, "Results fetched successfully", aiAnalysisService.getResultsForJob(jobId));
    }

    @GetMapping("/rankings")
    public ApiResponse<List<AnalysisDTO.RankingResponse>> getRankings(@RequestParam(required = false) Long jobId) {
        return new ApiResponse<>(true, "Candidate rankings fetched successfully", aiAnalysisService.getRankings(jobId));
    }
}
