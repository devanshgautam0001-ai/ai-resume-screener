package com.resumeai.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "analysis_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "match_score", nullable = false)
    private Integer matchScore;

    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;

    @Column(name = "skills_score", nullable = false)
    private Integer skillsScore;

    @Column(name = "experience_score", nullable = false)
    private Integer experienceScore;

    @Column(name = "education_score", nullable = false)
    private Integer educationScore;

    @Column(name = "ats_score", nullable = false)
    private Integer atsScore;

    @Column(name = "missing_skills_text", columnDefinition = "TEXT")
    private String missingSkillsText;

    @Column(name = "improvement_suggestions_text", columnDefinition = "TEXT")
    private String improvementSuggestionsText;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
