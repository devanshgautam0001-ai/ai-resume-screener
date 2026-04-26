package com.resumeai.repository;

import com.resumeai.model.ResumeSkillMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeSkillMappingRepository extends JpaRepository<ResumeSkillMapping, Long> {
    List<ResumeSkillMapping> findByResumeId(Long resumeId);
    void deleteByResumeId(Long resumeId);
    void deleteBySkillId(Long skillId);
}
