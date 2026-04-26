package com.resumeai.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RankingProcedureService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void refreshRankings(Long jobId) {
        entityManager.createNativeQuery("DELETE FROM candidate_rankings WHERE job_id = :jobId")
            .setParameter("jobId", jobId)
            .executeUpdate();

        entityManager.createNativeQuery("""
            INSERT INTO candidate_rankings (job_id, resume_id, rank_position, overall_score, ats_score, created_at)
            SELECT
                ar.job_id,
                ar.resume_id,
                DENSE_RANK() OVER (ORDER BY ar.overall_score DESC, ar.ats_score DESC, ar.created_at ASC) AS rank_position,
                ar.overall_score,
                ar.ats_score,
                CURRENT_TIMESTAMP
            FROM analysis_results ar
            WHERE ar.job_id = :jobId
            """)
            .setParameter("jobId", jobId)
            .executeUpdate();
    }
}
