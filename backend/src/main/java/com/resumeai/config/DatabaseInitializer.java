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
                    Skill.builder().name("Java").category("PROGRAMMING").build(),
                    Skill.builder().name("Spring Boot").category("FRAMEWORK").build(),
                    Skill.builder().name("React").category("FRAMEWORK").build(),
                    Skill.builder().name("JavaScript").category("PROGRAMMING").build(),
                    Skill.builder().name("Python").category("PROGRAMMING").build(),
                    Skill.builder().name("PostgreSQL").category("DATABASE").build(),
                    Skill.builder().name("Docker").category("DEVOPS").build(),
                    Skill.builder().name("Kubernetes").category("DEVOPS").build(),
                    Skill.builder().name("AWS").category("CLOUD").build(),
                    Skill.builder().name("Git").category("TOOLS").build(),
                    Skill.builder().name("Maven").category("TOOLS").build(),
                    Skill.builder().name("REST API").category("ARCHITECTURE").build(),
                    Skill.builder().name("Microservices").category("ARCHITECTURE").build(),
                    Skill.builder().name("TypeScript").category("PROGRAMMING").build(),
                    Skill.builder().name("Node.js").category("PROGRAMMING").build(),
                    Skill.builder().name("MongoDB").category("DATABASE").build(),
                    Skill.builder().name("Redis").category("DATABASE").build(),
                    Skill.builder().name("Linux").category("OS").build(),
                    Skill.builder().name("Agile").category("METHODOLOGY").build(),
                    Skill.builder().name("CI/CD").category("DEVOPS").build()
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