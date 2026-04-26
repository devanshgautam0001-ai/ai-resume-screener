package com.resumeai.controller;

import com.resumeai.dto.ApiResponse;
import com.resumeai.dto.ResumeDTO;
import com.resumeai.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(value = "/upload/{jobId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ResumeDTO.UploadResponse> upload(
        @PathVariable Long jobId,
        @RequestPart("files") MultipartFile[] files
    ) throws Exception {
        return new ApiResponse<>(true, "Resumes uploaded successfully", resumeService.uploadResumes(jobId, files));
    }

    @GetMapping
    public ApiResponse<List<ResumeDTO.ResumeResponse>> all() {
        return new ApiResponse<>(true, "Resumes fetched successfully", resumeService.findAll());
    }

    @GetMapping("/job/{jobId}")
    public ApiResponse<List<ResumeDTO.ResumeResponse>> byJob(@PathVariable Long jobId) {
        return new ApiResponse<>(true, "Job resumes fetched successfully", resumeService.findByJobId(jobId));
    }

    @GetMapping("/{id}")
    public ApiResponse<ResumeDTO.ResumeResponse> byId(@PathVariable Long id) {
        return new ApiResponse<>(true, "Resume fetched successfully", resumeService.findById(id));
    }
}
