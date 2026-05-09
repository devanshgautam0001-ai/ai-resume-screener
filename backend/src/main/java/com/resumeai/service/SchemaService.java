package com.resumeai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SchemaService {

    private static final Logger logger = LoggerFactory.getLogger(SchemaService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createSchemaForUser(String username) {
        String schemaName = "tenant_" + username.toLowerCase().replaceAll("[^a-z0-9]", "_");
        
        try {
            // Create the schema
            String createSchemaSql = "CREATE SCHEMA IF NOT EXISTS \"" + schemaName + "\"";
            jdbcTemplate.execute(createSchemaSql);
            logger.info("Created schema: {}", schemaName);
            
            logger.info("Schema created successfully for user: {}", username);
        } catch (Exception e) {
            logger.error("Error creating schema for user: {}", username, e);
            // Don't fail registration if schema creation fails
            logger.warn("Continuing without schema creation for user: {}", username);
        }
    }

    public String getSchemaNameForUser(String username) {
        return "tenant_" + username.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }
}