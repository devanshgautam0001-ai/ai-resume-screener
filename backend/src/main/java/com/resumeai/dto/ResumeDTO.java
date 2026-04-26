package com.resumeai.dto;

import java.time.Instant;
import java.util.List;

public class ResumeDTO {

    public record ResumeResponse(
        Long id,
        Long jobId,
        String candidateName,
        String email,
        String phone,
        String originalFileName,
        String status,
        List<String> skills,
        List<String> experiences,
        List<String> education,
        List<String> projects,
        Instant createdAt
    ) {
    }

    public record UploadResponse(
        Long jobId,
        List<ResumeResponse> resumes,
        String message
    ) {
    }
}
