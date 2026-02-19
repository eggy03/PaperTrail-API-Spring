package io.github.eggy03.papertrail.api.unit;

import io.github.eggy03.papertrail.api.dto.MessageLogContentDTO;
import io.github.eggy03.papertrail.api.entity.MessageLogContent;
import io.github.eggy03.papertrail.api.exceptions.MessageAlreadyLoggedException;
import io.github.eggy03.papertrail.api.exceptions.MessageNotFoundException;
import io.github.eggy03.papertrail.api.mapper.MessageLogContentMapper;
import io.github.eggy03.papertrail.api.repository.MessageLogContentRepository;
import io.github.eggy03.papertrail.api.service.MessageLogContentService;
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
class MessageLogContentServiceDoTest {

    @Mock
    private MessageLogContentRepository repository;

    @Mock
    private MessageLogContentMapper mapper;

    @InjectMocks
    private MessageLogContentService service;

    private MessageLogContent entity;
    private MessageLogContentDTO dto;

    @BeforeEach
    void setup() {

        Long messageId = 1245879561245252L;
        String messageContent = "Test Message";
        Long authorId = 8451322645685225L;

        entity = new MessageLogContent();
        entity.setMessageId(messageId);
        entity.setMessageContent(messageContent);
        entity.setAuthorId(authorId);

        dto = new MessageLogContentDTO();
        dto.setMessageId(messageId);
        dto.setMessageContent(messageContent);
        dto.setAuthorId(authorId);

    }

    @Test
    void doSaveMessage_success() {
        when(repository.existsById(dto.getMessageId())).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(entity);

        MessageLogContentDTO result = service.doSaveMessage(dto);

        assertThat(result).isEqualTo(dto);

        verify(repository).saveAndFlush(entity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void doSaveMessage_exists_throwsException() {
        when(repository.existsById(dto.getMessageId())).thenReturn(true);

        assertThrows(MessageAlreadyLoggedException.class, ()-> service.doSaveMessage(dto));

        verify(repository, never()).saveAndFlush(any());
        verify(mapper, never()).toEntity(dto);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void doFindMessageById_success() {
        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(dto);

        MessageLogContentDTO result = service.doFindMessageById(dto.getMessageId());

        assertThat(result).isEqualTo(dto);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void doFindMessageById_notFound_throwsException() {
        Long messageId = dto.getMessageId();
        when(repository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, ()-> service.doFindMessageById(messageId));

        verify(mapper, never()).toDTO(any());
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void doUpdateMessage_success() {
        when(repository.existsById(dto.getMessageId())).thenReturn(true);
        when(mapper.toEntity(dto)).thenReturn(entity);

        MessageLogContentDTO result = service.doUpdateMessage(dto);

        assertThat(result).isEqualTo(dto);
        verify(repository).saveAndFlush(entity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void doUpdateMessage_notFound_throwsException() {
        when(repository.existsById(dto.getMessageId())).thenReturn(false);

        assertThrows(MessageNotFoundException.class, ()-> service.doUpdateMessage(dto));

        verify(mapper, never()).toDTO(any());
        verify(repository, never()).saveAndFlush(any());
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void doDeleteMessage_success() {
        when(repository.existsById(dto.getMessageId())).thenReturn(true);

        service.doDeleteMessage(dto.getMessageId());

        verify(repository).deleteById(dto.getMessageId());
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void doDeleteMessage_notFound_throwsException() {
        Long messageId = dto.getMessageId();
        when(repository.existsById(messageId)).thenReturn(false);

        assertThrows(MessageNotFoundException.class, ()-> service.doDeleteMessage(messageId));

        verify(repository, never()).delete(any());
        verifyNoMoreInteractions(repository, mapper);
    }


}
