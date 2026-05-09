package com.resumeai.repository;

import com.resumeai.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByJobIdOrderByCreatedAtDesc(Long jobId);
    List<Resume> findByUploadedByIdOrderByCreatedAtDesc(Long userId);
    Optional<Resume> findByIdAndUploadedById(Long id, Long userId);
    List<Resume> findByJobIdAndUploadedByIdOrderByCreatedAtDesc(Long jobId, Long userId);
}
