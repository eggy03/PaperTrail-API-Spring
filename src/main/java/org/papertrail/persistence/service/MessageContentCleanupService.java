package org.papertrail.persistence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.papertrail.persistence.repository.MessageLogContentRepository;
import org.papertrail.persistence.util.AnsiColor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageContentCleanupService {

    private final MessageLogContentRepository repository;

    // Run daily at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupOldMessages() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(30);
        int deletedMessageCount = repository.deleteOlderThan(cutoff);
        log.info("{}Message Content Cleanup Service- Cleaned up {} messages older than {}{}", AnsiColor.GREEN, deletedMessageCount, cutoff, AnsiColor.RESET);
    }
}
