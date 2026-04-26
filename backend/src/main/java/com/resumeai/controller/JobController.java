package com.resumeai.controller;

import com.resumeai.dto.ApiResponse;
import com.resumeai.dto.JobDTO;
import com.resumeai.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/create")
    public ApiResponse<JobDTO.JobResponse> create(@Valid @RequestBody JobDTO.CreateJobRequest request) {
        return new ApiResponse<>(true, "Job description created successfully", jobService.create(request));
    }

    @GetMapping
    public ApiResponse<List<JobDTO.JobResponse>> all() {
        return new ApiResponse<>(true, "Jobs fetched successfully", jobService.getAll());
    }

    @GetMapping("/mine")
    public ApiResponse<List<JobDTO.JobResponse>> mine() {
        return new ApiResponse<>(true, "Current user jobs fetched successfully", jobService.getCurrentUserJobs());
    }
}
