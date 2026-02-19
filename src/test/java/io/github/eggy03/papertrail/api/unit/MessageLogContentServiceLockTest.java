package io.github.eggy03.papertrail.api.unit;

import io.github.eggy03.papertrail.api.dto.MessageLogContentDTO;
import io.github.eggy03.papertrail.api.mapper.MessageLogContentMapper;
import io.github.eggy03.papertrail.api.repository.MessageLogContentRepository;
import io.github.eggy03.papertrail.api.service.MessageLogContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageLogContentServiceLockTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @Mock
    private MessageLogContentRepository repository;

    @Mock
    private MessageLogContentMapper mapper;

    @Mock
    private ObjectProvider<MessageLogContentService> serviceObjectProvider;

    @Spy
    @InjectMocks
    private MessageLogContentService service;

    private MessageLogContentDTO dto;


    @BeforeEach
    void setup() {
        dto = new MessageLogContentDTO();
        when(serviceObjectProvider.getIfAvailable()).thenReturn(service);
    }

    @Test
    void saveMessage_acquireAndReleaseLock() {
        when(redissonClient.getFairLock(String.valueOf(dto.getMessageId()))).thenReturn(rLock);
        doReturn(dto).when(service).doSaveMessage(dto);

        MessageLogContentDTO result = service.saveMessage(dto);

        assertThat(result).isEqualTo(dto);

        verify(rLock).lock();
        verify(service).doSaveMessage(dto);
        verify(rLock).unlock();
    }

    @Test
    void findMessageById_acquireAndReleaseLock() {
        when(redissonClient.getFairLock(String.valueOf(dto.getMessageId()))).thenReturn(rLock);
        doReturn(dto).when(service).doFindMessageById(dto.getMessageId());

        MessageLogContentDTO result = service.findMessageById(dto.getMessageId());

        assertThat(result).isEqualTo(dto);

        verify(rLock).lock();
        verify(service).doFindMessageById(dto.getMessageId());
        verify(rLock).unlock();

    }

    @Test
    void updateMessage_acquireAndReleaseLock() {
        when(redissonClient.getFairLock(String.valueOf(dto.getMessageId()))).thenReturn(rLock);
        doReturn(dto).when(service).doUpdateMessage(dto);

        MessageLogContentDTO result = service.updateMessage(dto);

        assertThat(result).isEqualTo(dto);

        verify(rLock).lock();
        verify(service).doUpdateMessage(dto);
        verify(rLock).unlock();
    }

    @Test
    void deleteMessage_acquireAndReleaseLock() {
        when(redissonClient.getFairLock(String.valueOf(dto.getMessageId()))).thenReturn(rLock);
        doNothing().when(service).doDeleteMessage(dto.getMessageId());

        service.deleteMessage(dto.getMessageId());

        verify(rLock).lock();
        verify(service).doDeleteMessage(dto.getMessageId());
        verify(rLock).unlock();
    }
}

