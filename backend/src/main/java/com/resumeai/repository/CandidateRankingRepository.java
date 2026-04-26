package com.resumeai.repository;

import com.resumeai.model.CandidateRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRankingRepository extends JpaRepository<CandidateRanking, Long> {
    List<CandidateRanking> findByJobIdOrderByRankPositionAsc(Long jobId);
    List<CandidateRanking> findAllByOrderByCreatedAtDesc();
    List<CandidateRanking> findTop5ByOrderByOverallScoreDesc();
    void deleteByJobId(Long jobId);
}
