package io.github.eggy03.papertrail.api.repository;

import io.github.eggy03.papertrail.api.entity.MessageLogContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

public interface MessageLogContentRepository extends JpaRepository<MessageLogContent, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM MessageLogContent m WHERE m.createdAt < :cutoff")
    int deleteOlderThan(OffsetDateTime cutoff);

}
