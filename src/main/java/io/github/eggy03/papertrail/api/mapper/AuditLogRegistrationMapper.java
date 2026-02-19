package io.github.eggy03.papertrail.api.mapper;

import io.github.eggy03.papertrail.api.dto.AuditLogRegistrationDTO;
import io.github.eggy03.papertrail.api.entity.AuditLogRegistration;
import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface AuditLogRegistrationMapper {

    AuditLogRegistration toEntity(AuditLogRegistrationDTO auditLogRegistrationDTO);

    AuditLogRegistrationDTO toDTO(AuditLogRegistration auditLogRegistration);
}
