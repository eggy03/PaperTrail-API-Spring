package io.github.eggy03.papertrail.api.service;

import io.github.eggy03.papertrail.api.dto.MessageLogContentDTO;
import io.github.eggy03.papertrail.api.entity.MessageLogContent;
import io.github.eggy03.papertrail.api.exceptions.MessageAlreadyLoggedException;
import io.github.eggy03.papertrail.api.exceptions.MessageNotFoundException;
import io.github.eggy03.papertrail.api.mapper.MessageLogContentMapper;
import io.github.eggy03.papertrail.api.repository.MessageLogContentRepository;
import io.github.eggy03.papertrail.api.util.AnsiColor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageLogContentService {

    private final MessageLogContentMapper mapper;
    private final MessageLogContentRepository repository;
    private final RedissonClient redissonClient;

    private final ObjectProvider<MessageLogContentService> selfProvider;

    private MessageLogContentService self() {
        return selfProvider.getIfAvailable();
    }

    public MessageLogContentDTO saveMessage(MessageLogContentDTO messageLogContentDTO){
        RLock lock = redissonClient.getFairLock(String.valueOf(messageLogContentDTO.getMessageId()));

        lock.lock();
        log.info("Acquired SAVE lock for messageID {} with active lock count {}", lock.getName(), lock.getHoldCount());

        try {
            return self().doSaveMessage(messageLogContentDTO);
        } finally {
            lock.unlock();
            log.info("Released SAVE lock for messageID {} with active lock count {}", lock.getName(), lock.getHoldCount());
        }
    }

    public MessageLogContentDTO findMessageById(Long messageId) {
        RLock lock = redissonClient.getFairLock(String.valueOf(messageId));
        lock.lock();
        log.info("Acquired READ lock for messageID {} with active lock count {}", lock.getName(), lock.getHoldCount());

        try {
            return self().doFindMessageById(messageId);
        } finally {
            lock.unlock();
            log.info("Released READ lock for messageID {} with active lock count {}", lock.getName(), lock.getHoldCount());
        }
    }

    public MessageLogContentDTO updateMessage(MessageLogContentDTO updatedMessage) {
        RLock lock = redissonClient.getFairLock(String.valueOf(updatedMessage.getMessageId()));
        lock.lock();
        log.info("Acquired UPDATE lock for messageID {} with active lock count {}", lock.getName(), lock.getHoldCount());

        try {
            return self().doUpdateMessage(updatedMessage);
        } finally {
            lock.unlock();
            log.info("Released UPDATE lock for messageID {} with active lock count {}", lock.getName(), lock.getHoldCount());
        }
    }

    public void deleteMessage(Long messageId) {
        RLock lock = redissonClient.getFairLock(String.valueOf(messageId));
        lock.lock();
        log.info("Acquired DELETE lock for messageID {} with active lock count {}", lock.getName(), lock.getHoldCount());

        try {
            self().doDeleteMessage(messageId);
        } finally {
            lock.unlock();
            log.info("Released DELETE lock for messageID {} with active lock count {}", lock.getName(), lock.getHoldCount());
        }
    }

    @Transactional
    @CachePut(value = "messageContent", key = "#messageLogContentDTO.messageId")
    public MessageLogContentDTO doSaveMessage(MessageLogContentDTO messageLogContentDTO) {

        log.info("{}Attempting to save message with ID={}{}", AnsiColor.YELLOW, messageLogContentDTO.getMessageId(), AnsiColor.RESET);

        if(repository.existsById(messageLogContentDTO.getMessageId())){
            throw new MessageAlreadyLoggedException("Message has already been logged before. A logged message can only be updated or deleted.");
        }

        MessageLogContent messageLogContent = mapper.toEntity(messageLogContentDTO);
        repository.saveAndFlush(messageLogContent);

        log.info("{}Successfully saved message with ID={}{}", AnsiColor.GREEN, messageLogContentDTO.getMessageId(), AnsiColor.RESET);
        return messageLogContentDTO;
    }

    @Transactional (readOnly = true)
    @Cacheable(value = "messageContent", key = "#messageId")
    public MessageLogContentDTO doFindMessageById(Long messageId) {

        log.info("{}Cache MISS - Fetching message with ID={}{}", AnsiColor.YELLOW, messageId, AnsiColor.RESET);
        MessageLogContent messageLogContent = repository.findById(messageId)
                .orElseThrow(()-> new MessageNotFoundException("Message with the given ID hasn't been logged before"));

        log.info("{}Found message with ID={}{}", AnsiColor.BLUE, messageId, AnsiColor.RESET);
        return mapper.toDTO(messageLogContent);
    }

    @Transactional
    @CachePut(value = "messageContent", key = "#updatedMessage.messageId")
    public MessageLogContentDTO doUpdateMessage(MessageLogContentDTO updatedMessage) {

        log.info("{}Attempting to update message with ID={}{}", AnsiColor.YELLOW, updatedMessage.getMessageId(), AnsiColor.RESET);
        if(!repository.existsById(updatedMessage.getMessageId())){
            throw new MessageNotFoundException("Message with the given ID hasn't been logged before");
        }

        MessageLogContent updatedMessageEntity = mapper.toEntity(updatedMessage);
        repository.saveAndFlush(updatedMessageEntity);
        log.info("{}Successfully updated message with ID={}{}", AnsiColor.GREEN, updatedMessage.getMessageId(), AnsiColor.RESET);
        return updatedMessage;

    }

    @Transactional
    @CacheEvict(value = "messageContent", key = "#messageId")
    public void doDeleteMessage(Long messageId) {

        log.info("{}Attempting to delete message with ID={}{}", AnsiColor.YELLOW, messageId, AnsiColor.RESET);
        if(!repository.existsById(messageId)){
            throw new MessageNotFoundException("Message hasn't been logged or the ID is invalid");
        }

        repository.deleteById(messageId);
        log.info("{}Successfully deleted message with ID={}{}", AnsiColor.GREEN, messageId, AnsiColor.RESET);
    }
}
