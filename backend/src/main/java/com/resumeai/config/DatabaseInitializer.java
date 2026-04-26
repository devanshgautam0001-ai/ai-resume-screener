package com.resumeai.config;

import com.resumeai.model.Skill;
import com.resumeai.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {

    private final SkillRepository skillRepository;

    @Bean
    @Profile("dev")
    @Transactional
    public CommandLineRunner initializeDatabase() {
        return args -> {
            log.info("Initializing database with seed data...");
            
            // Seed skills if table is empty
            if (skillRepository.count() == 0) {
                List<Skill> skills = Arrays.asList(
                    new Skill(null, "Java", "PROGRAMMING"),
                    new Skill(null, "Spring Boot", "FRAMEWORK"),
                    new Skill(null, "React", "FRAMEWORK"),
                    new Skill(null, "JavaScript", "PROGRAMMING"),
                    new Skill(null, "Python", "PROGRAMMING"),
                    new Skill(null, "PostgreSQL", "DATABASE"),
                    new Skill(null, "Docker", "DEVOPS"),
                    new Skill(null, "Kubernetes", "DEVOPS"),
                    new Skill(null, "AWS", "CLOUD"),
                    new Skill(null, "Git", "TOOLS"),
                    new Skill(null, "Maven", "TOOLS"),
                    new Skill(null, "REST API", "ARCHITECTURE"),
                    new Skill(null, "Microservices", "ARCHITECTURE"),
                    new Skill(null, "TypeScript", "PROGRAMMING"),
                    new Skill(null, "Node.js", "PROGRAMMING"),
                    new Skill(null, "MongoDB", "DATABASE"),
                    new Skill(null, "Redis", "DATABASE"),
                    new Skill(null, "Linux", "OS"),
                    new Skill(null, "Agile", "METHODOLOGY"),
                    new Skill(null, "CI/CD", "DEVOPS")
                );
                
                skillRepository.saveAll(skills);
                log.info("Seeded {} skills into database", skills.size());
            } else {
                log.info("Skills table already contains {} records", skillRepository.count());
            }
            
            log.info("Database initialization completed successfully");
        };
    }
}