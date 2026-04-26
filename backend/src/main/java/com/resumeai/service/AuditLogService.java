package com.resumeai.service;

import com.resumeai.model.AuditLog;
import com.resumeai.model.User;
import com.resumeai.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, String entityType, Long entityId, String action, String details) {
        auditLogRepository.save(AuditLog.builder()
            .user(user)
            .entityType(entityType)
            .entityId(entityId)
            .action(action)
            .details(details)
            .build());
    }
}
