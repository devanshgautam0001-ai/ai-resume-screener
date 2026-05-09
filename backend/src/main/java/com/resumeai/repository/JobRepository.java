package com.resumeai.repository;

import com.resumeai.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findAllByCreatedByIdOrderByCreatedAtDesc(Long userId);
    List<Job> findByStatus(String status);
    Optional<Job> findByIdAndCreatedById(Long id, Long userId);
}
