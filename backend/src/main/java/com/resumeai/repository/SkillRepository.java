package com.resumeai.repository;

import com.resumeai.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByNameIgnoreCase(String name);
    
    // Find all skills by name (case-insensitive) to handle duplicates
    List<Skill> findAllByNameIgnoreCase(String name);
    
    // Find first skill by name (case-insensitive) to avoid unique result errors
    Optional<Skill> findFirstByNameIgnoreCase(String name);
}
