package io.github.eggy03.papertrail.api.repository;

import io.github.eggy03.papertrail.api.entity.AuditLogRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRegistrationRepository extends JpaRepository<AuditLogRegistration, Long> {

}
