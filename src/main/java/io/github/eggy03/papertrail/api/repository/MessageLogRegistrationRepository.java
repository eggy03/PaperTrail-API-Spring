package io.github.eggy03.papertrail.api.repository;

import io.github.eggy03.papertrail.api.entity.MessageLogRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageLogRegistrationRepository extends JpaRepository<MessageLogRegistration, Long> {

}
