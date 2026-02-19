package io.github.eggy03.papertrail.api.service;

import io.github.eggy03.papertrail.api.dto.MessageLogRegistrationDTO;
import io.github.eggy03.papertrail.api.entity.MessageLogRegistration;
import io.github.eggy03.papertrail.api.exceptions.GuildAlreadyRegisteredException;
import io.github.eggy03.papertrail.api.exceptions.GuildNotFoundException;
import io.github.eggy03.papertrail.api.mapper.MessageLogRegistrationMapper;
import io.github.eggy03.papertrail.api.repository.MessageLogRegistrationRepository;
import io.github.eggy03.papertrail.api.util.AnsiColor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageLogRegistrationService {

    private final MessageLogRegistrationMapper mapper;
    private final MessageLogRegistrationRepository repository;

    @Transactional
    @CachePut(value = "messageLog", key = "#messageLogRegistrationDTO.guildId")
    public MessageLogRegistrationDTO registerGuild(MessageLogRegistrationDTO messageLogRegistrationDTO){

        log.info("{}Attempting to register message log guild with ID={}{}", AnsiColor.YELLOW, messageLogRegistrationDTO.getGuildId(), AnsiColor.RESET);
        if(repository.existsById(messageLogRegistrationDTO.getGuildId())){
            throw new GuildAlreadyRegisteredException("Guild already registered for message logging");
        }

        MessageLogRegistration messageLogRegistration = mapper.toEntity(messageLogRegistrationDTO);
        repository.saveAndFlush(messageLogRegistration);
        log.info("{}Successfully registered message log guild with ID={}{}", AnsiColor.GREEN, messageLogRegistrationDTO.getGuildId(), AnsiColor.RESET);
        return messageLogRegistrationDTO;
    }

    @Transactional (readOnly = true)
    @Cacheable(value = "messageLog", key = "#guildId")
    public MessageLogRegistrationDTO findByGuild(Long guildId){

        log.info("{}Cache MISS - Fetching message log guild with ID={}{}", AnsiColor.YELLOW, guildId, AnsiColor.RESET);
        MessageLogRegistration messageLogRegistration = repository.findById(guildId)
                .orElseThrow(()-> new GuildNotFoundException("Guild is not registered for message logging"));

        log.info("{}Found message log guild with ID={}{}", AnsiColor.BLUE, guildId, AnsiColor.RESET);
        return mapper.toDTO(messageLogRegistration);
    }

    @Transactional
    @CachePut(value = "messageLog", key = "#updatedDTO.guildId")
    public MessageLogRegistrationDTO updateGuild (MessageLogRegistrationDTO updatedDTO) {

        log.info("{}Attempting to update message log guild with ID={}{}", AnsiColor.YELLOW, updatedDTO.getGuildId(), AnsiColor.RESET);
        if (!repository.existsById(updatedDTO.getGuildId())) {
            throw new GuildNotFoundException("Guild is not registered for message logging");
        }

        repository.save(mapper.toEntity(updatedDTO));
        log.info("{}Successfully updated message log guild with ID={}{}", AnsiColor.GREEN, updatedDTO.getGuildId(), AnsiColor.RESET);
        return updatedDTO;
    }

    @Transactional
    @CacheEvict(value = "messageLog", key = "#guildId")
    public void unregisterGuild(Long guildId){

        log.info("{}Attempting to unregister message log guild with ID={}{}", AnsiColor.YELLOW, guildId, AnsiColor.RESET);
        MessageLogRegistration messageLogRegistration = repository.findById(guildId)
                .orElseThrow(()-> new GuildNotFoundException("Guild is not registered for message logging"));

        repository.delete(messageLogRegistration);
        log.info("{}Successfully unregistered message log guild with ID={}{}", AnsiColor.GREEN, guildId, AnsiColor.RESET);
    }
}
