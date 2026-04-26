package com.resumeai.repository;

import com.resumeai.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    List<AnalysisResult> findByJobIdOrderByOverallScoreDesc(Long jobId);
    Optional<AnalysisResult> findByResumeIdAndJobId(Long resumeId, Long jobId);
    void deleteByJobId(Long jobId);
}
