package com.resumeai.repository;

import com.resumeai.model.JobSkillMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSkillMappingRepository extends JpaRepository<JobSkillMapping, Long> {
    List<JobSkillMapping> findByJobId(Long jobId);
    void deleteByJobId(Long jobId);
    void deleteBySkillId(Long skillId);
}
