package com.resumeai.service;

import com.resumeai.model.Job;
import com.resumeai.model.Skill;
import com.resumeai.model.User;
import com.resumeai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseCleanupService {

    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ResumeSkillMappingRepository resumeSkillMappingRepository;
    private final JobSkillMappingRepository jobSkillMappingRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final CandidateRankingRepository candidateRankingRepository;

    @Transactional
    public String cleanupDuplicates() {
        StringBuilder report = new StringBuilder();
        
        report.append("=== Database Cleanup Report ===\n");
        
        // Clean up duplicate skills
        int skillsRemoved = cleanupDuplicateSkills();
        report.append(String.format("Duplicate skills removed: %d\n", skillsRemoved));
        
        // Clean up duplicate jobs
        int jobsRemoved = cleanupDuplicateJobs();
        report.append(String.format("Duplicate jobs removed: %d\n", jobsRemoved));
        
        // Clean up duplicate users
        int usersRemoved = cleanupDuplicateUsers();
        report.append(String.format("Duplicate users removed: %d\n", usersRemoved));
        
        // Summary
        report.append(String.format("\nFinal counts - Skills: %d, Jobs: %d, Users: %d\n",
            skillRepository.count(), jobRepository.count(), userRepository.count()));
        
        log.info(report.toString());
        return report.toString();
    }

    private int cleanupDuplicateSkills() {
        List<Skill> allSkills = skillRepository.findAll();
        Map<String, List<Skill>> skillsByName = allSkills.stream()
            .collect(Collectors.groupingBy(skill -> skill.getName().toLowerCase()));
        
        int removed = 0;
        
        for (Map.Entry<String, List<Skill>> entry : skillsByName.entrySet()) {
            List<Skill> duplicates = entry.getValue();
            if (duplicates.size() > 1) {
                // Keep first, remove rest
                Skill keep = duplicates.get(0);
                for (int i = 1; i < duplicates.size(); i++) {
                    Skill remove = duplicates.get(i);
                    resumeSkillMappingRepository.deleteBySkillId(remove.getId());
                    jobSkillMappingRepository.deleteBySkillId(remove.getId());
                    skillRepository.delete(remove);
                    removed++;
                    log.info("Removed duplicate skill: {} (id: {})", remove.getName(), remove.getId());
                }
            }
        }
        
        return removed;
    }

    private int cleanupDuplicateJobs() {
        List<Job> allJobs = jobRepository.findAll();
        Map<String, List<Job>> jobsByKey = allJobs.stream()
            .collect(Collectors.groupingBy(job -> 
                String.format("%d_%s_%s", 
                    job.getCreatedBy().getId(), 
                    job.getTitle().toLowerCase(), 
                    job.getCompany().toLowerCase())));
        
        int removed = 0;
        
        for (Map.Entry<String, List<Job>> entry : jobsByKey.entrySet()) {
            List<Job> duplicates = entry.getValue();
            if (duplicates.size() > 1) {
                // Keep first, remove rest
                Job keep = duplicates.get(0);
                for (int i = 1; i < duplicates.size(); i++) {
                    Job remove = duplicates.get(i);
                    // Delete dependent records first
                    analysisResultRepository.deleteByJobId(remove.getId());
                    candidateRankingRepository.deleteByJobId(remove.getId());
                    jobSkillMappingRepository.deleteByJobId(remove.getId());
                    jobRepository.delete(remove);
                    removed++;
                    log.info("Removed duplicate job: {} at {} (id: {})", 
                        remove.getTitle(), remove.getCompany(), remove.getId());
                }
            }
        }
        
        return removed;
    }

    private int cleanupDuplicateUsers() {
        List<User> allUsers = userRepository.findAll();
        Map<String, List<User>> usersByEmail = allUsers.stream()
            .collect(Collectors.groupingBy(user -> user.getEmail().toLowerCase()));
        
        int removed = 0;
        
        for (Map.Entry<String, List<User>> entry : usersByEmail.entrySet()) {
            List<User> duplicates = entry.getValue();
            if (duplicates.size() > 1) {
                // Keep first, remove rest
                User keep = duplicates.get(0);
                for (int i = 1; i < duplicates.size(); i++) {
                    User remove = duplicates.get(i);
                    userRepository.delete(remove);
                    removed++;
                    log.info("Removed duplicate user: {} (id: {})", remove.getEmail(), remove.getId());
                }
            }
        }
        
        return removed;
    }
}