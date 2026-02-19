package io.github.eggy03.papertrail.api.mapper;

import io.github.eggy03.papertrail.api.dto.MessageLogRegistrationDTO;
import io.github.eggy03.papertrail.api.entity.MessageLogRegistration;
import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface MessageLogRegistrationMapper {

    MessageLogRegistration toEntity (MessageLogRegistrationDTO messageLogRegistrationDTO);

    MessageLogRegistrationDTO toDTO (MessageLogRegistration messageLogRegistration);
}
