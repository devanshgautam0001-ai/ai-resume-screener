package com.resumeai.repository;

import com.resumeai.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    List<AnalysisResult> findByJobIdOrderByOverallScoreDesc(Long jobId);
    Optional<AnalysisResult> findByResumeIdAndJobId(Long resumeId, Long jobId);
    void deleteByJobId(Long jobId);
    
    @Query("SELECT ar FROM AnalysisResult ar JOIN ar.job j WHERE j.createdBy.id = :userId AND ar.job.id = :jobId")
    List<AnalysisResult> findByJobIdAndUserIdOrderByOverallScoreDesc(@Param("jobId") Long jobId, @Param("userId") Long userId);
    
    @Query("SELECT ar FROM AnalysisResult ar JOIN ar.resume r JOIN ar.job j WHERE r.uploadedBy.id = :userId AND j.id = :jobId")
    List<AnalysisResult> findByJobIdAndResumeUserIdOrderByOverallScoreDesc(@Param("jobId") Long jobId, @Param("userId") Long userId);
}
