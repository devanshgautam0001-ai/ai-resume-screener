package com.resumeai.controller;

import com.resumeai.dto.AnalysisDTO;
import com.resumeai.dto.ApiResponse;
import com.resumeai.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AiAnalysisService aiAnalysisService;

    @PostMapping("/run/{resumeId}/{jobId}")
    public ApiResponse<AnalysisDTO.AnalysisResponse> analyze(@PathVariable Long resumeId, @PathVariable Long jobId) {
        return new ApiResponse<>(true, "Analysis completed successfully", aiAnalysisService.analyze(resumeId, jobId));
    }
}
