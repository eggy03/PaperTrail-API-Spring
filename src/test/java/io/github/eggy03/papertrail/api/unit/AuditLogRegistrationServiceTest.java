package io.github.eggy03.papertrail.api.unit;

import io.github.eggy03.papertrail.api.dto.AuditLogRegistrationDTO;
import io.github.eggy03.papertrail.api.entity.AuditLogRegistration;
import io.github.eggy03.papertrail.api.exceptions.GuildAlreadyRegisteredException;
import io.github.eggy03.papertrail.api.exceptions.GuildNotFoundException;
import io.github.eggy03.papertrail.api.mapper.AuditLogRegistrationMapper;
import io.github.eggy03.papertrail.api.repository.AuditLogRegistrationRepository;
import io.github.eggy03.papertrail.api.service.AuditLogRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuditLogRegistrationServiceTest {

    @Mock
    private AuditLogRegistrationRepository repository;

    @Mock
    private AuditLogRegistrationMapper mapper;

    @InjectMocks
    private AuditLogRegistrationService service;

    private AuditLogRegistrationDTO dto;
    private AuditLogRegistration entity;

    private static final Long GUILD_ID = 124587145126L;
    private static final Long CHANNEL_ID = 541812154121L;

    @BeforeEach
    void setup() {
        dto = new AuditLogRegistrationDTO();
        dto.setGuildId(GUILD_ID);
        dto.setChannelId(CHANNEL_ID);

        entity = new AuditLogRegistration();
        entity.setGuildId(GUILD_ID);
        entity.setChannelId(CHANNEL_ID);
    }

    @Test
    void registerGuild_success() {
        when(repository.existsById(dto.getGuildId())).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(entity);

        AuditLogRegistrationDTO result = service.registerGuild(dto);

        assertThat(result).isEqualTo(dto);
        verify(repository).saveAndFlush(entity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test void registerGuild_exists_throwsException() {
        when(repository.existsById(dto.getGuildId())).thenReturn(true);

        assertThrows(GuildAlreadyRegisteredException.class, ()-> service.registerGuild(dto));

        verify(mapper, never()).toEntity(any());
        verify(repository, never()).saveAndFlush(any());
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void findByGuild_success() {
        when(repository.findById(dto.getGuildId())).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(dto);

        AuditLogRegistrationDTO result = service.findByGuild(dto.getGuildId());

        assertThat(result).isEqualTo(dto);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void findByGuild_notFound_throwsException() {
        Long guildId = dto.getGuildId();
        when(repository.findById(guildId)).thenReturn(Optional.empty());

        assertThrows(GuildNotFoundException.class, ()-> service.findByGuild(guildId));

        verify(mapper, never()).toDTO(any());
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void updateGuild_success() {
        when(repository.existsById(dto.getGuildId())).thenReturn(true);
        when(mapper.toEntity(dto)).thenReturn(entity);

        AuditLogRegistrationDTO result = service.updateGuild(dto);

        assertThat(result).isEqualTo(dto);
        verify(repository).save(entity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void updateGuild_notFound_throwsException() {
        when(repository.existsById(dto.getGuildId())).thenReturn(false);

        assertThrows(GuildNotFoundException.class, ()-> service.updateGuild(dto));

        verify(mapper, never()).toDTO(any());
        verify(repository, never()).save(any());
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void unregisterGuild_success() {
        when(repository.findById(dto.getGuildId())).thenReturn(Optional.of(entity));

        service.unregisterGuild(dto.getGuildId());

        verify(repository).delete(entity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void unregisterGuild_notFound_throwsException() {
        Long guildId = dto.getGuildId();
        when(repository.findById(guildId)).thenReturn(Optional.empty());

        assertThrows(GuildNotFoundException.class, ()-> service.unregisterGuild(guildId));

        verify(repository, never()).delete(any());
        verifyNoMoreInteractions(repository, mapper);
    }
}
