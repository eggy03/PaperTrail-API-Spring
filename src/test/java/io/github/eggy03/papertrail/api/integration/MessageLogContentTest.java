package io.github.eggy03.papertrail.api.integration;

import io.github.eggy03.papertrail.api.dto.MessageLogContentDTO;
import io.github.eggy03.papertrail.api.repository.MessageLogContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("dev")
@Slf4j
class MessageLogContentTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private MessageLogContentRepository repository;

    @Autowired
    private CacheManager cacheManager;

    MessageLogContentDTO body;
    private static final Long MESSAGE_ID = 124587145126L;
    private static final String MESSAGE_CONTENT = "text";
    private static final Long AUTHOR_ID = 541812154121L;

    private static final String BASE_URL = "/api/v1/content/message";

    private static RedisServer redisServer;

    @BeforeAll
    static void startRedis() throws IOException {
        redisServer = RedisServer.newRedisServer().build();
        redisServer.start();
    }

    @AfterAll
    static void stopRedis() throws IOException {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    @BeforeEach
    void loadEntities(){
        body = new MessageLogContentDTO();
        body.setMessageId(MESSAGE_ID);
        body.setMessageContent(MESSAGE_CONTENT);
        body.setAuthorId(AUTHOR_ID);
    }

    @BeforeEach
    void clearState() {
        repository.deleteAll();
        Objects.requireNonNull(cacheManager.getCache("messageContent")).clear();
    }

    @Test
    void saveMessage_success() {

        client.post()
                .uri(BASE_URL)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MessageLogContentDTO.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getMessageId()).isEqualTo(MESSAGE_ID);
                    assertThat(response.getMessageContent()).isEqualTo(MESSAGE_CONTENT);
                    assertThat(response.getAuthorId()).isEqualTo(AUTHOR_ID);
                });

    }

    @Test
    void saveMessage_exists_throwsException() {

        client.post()
                .uri(BASE_URL)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MessageLogContentDTO.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getMessageId()).isEqualTo(MESSAGE_ID);
                    assertThat(response.getMessageContent()).isEqualTo(MESSAGE_CONTENT);
                    assertThat(response.getAuthorId()).isEqualTo(AUTHOR_ID);
                });

        client.post()
                .uri(BASE_URL)
                .bodyValue(body)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void saveMessage_malformed_throwsException() {
        body.setMessageId(null);

        client.post()
                .uri(BASE_URL)
                .bodyValue(body)
                .exchange()
                .expectStatus().isEqualTo(400);

        body.setMessageId(123456789012L);
        body.setAuthorId(null);

        client.post()
                .uri(BASE_URL)
                .bodyValue(body)
                .exchange()
                .expectStatus().isEqualTo(400);
    }

    @Test
    void findMessage_success() {

        client.post()
                .uri(BASE_URL)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MessageLogContentDTO.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getMessageId()).isEqualTo(MESSAGE_ID);
                    assertThat(response.getMessageContent()).isEqualTo(MESSAGE_CONTENT);
                    assertThat(response.getAuthorId()).isEqualTo(AUTHOR_ID);
                });

        client.get()
                .uri(BASE_URL+"/"+MESSAGE_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MessageLogContentDTO.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getMessageId()).isEqualTo(MESSAGE_ID);
                    assertThat(response.getMessageContent()).isEqualTo(MESSAGE_CONTENT);
                    assertThat(response.getAuthorId()).isEqualTo(AUTHOR_ID);
                });

    }

    @Test
    void findMessage_notFound_throwsException() {
        client.get()
                .uri(BASE_URL+"/"+MESSAGE_ID)
                .exchange()
                .expectStatus().isEqualTo(404);
    }

    @Test
    void findMessage_malformed_throwsException() {
        client.get()
                .uri(BASE_URL + "/notALong")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateMessage_success() {
        String updatedContent = "updated text";

        client.post()
                .uri(BASE_URL)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MessageLogContentDTO.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getMessageId()).isEqualTo(MESSAGE_ID);
                    assertThat(response.getMessageContent()).isEqualTo(MESSAGE_CONTENT);
                    assertThat(response.getAuthorId()).isEqualTo(AUTHOR_ID);
                });

        body.setMessageContent(updatedContent);

        client.put()
                .uri(BASE_URL)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MessageLogContentDTO.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getMessageId()).isEqualTo(MESSAGE_ID);
                    assertThat(response.getMessageContent()).isEqualTo(updatedContent);
                    assertThat(response.getAuthorId()).isEqualTo(AUTHOR_ID);
                });

    }

    @Test
    void updateMessage_notFound_throwsException() {

        client.put()
                .uri(BASE_URL)
                .bodyValue(body)
                .exchange()
                .expectStatus().isEqualTo(404);

    }

    @Test
    void updateMessage_malformed_throwsException() {
        body.setMessageId(null);

        client.put()
                .uri(BASE_URL)
                .bodyValue(body)
                .exchange()
                .expectStatus().isEqualTo(400);

        body.setMessageId(MESSAGE_ID);
        body.setAuthorId(null);

        client.put()
                .uri(BASE_URL)
                .bodyValue(body)
                .exchange()
                .expectStatus().isEqualTo(400);
    }

    @Test
    void deleteMessage_success() {

        client.post()
                .uri(BASE_URL)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MessageLogContentDTO.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getMessageId()).isEqualTo(MESSAGE_ID);
                    assertThat(response.getMessageContent()).isEqualTo(MESSAGE_CONTENT);
                    assertThat(response.getAuthorId()).isEqualTo(AUTHOR_ID);
                });

        client.delete()
                .uri(BASE_URL+"/"+MESSAGE_ID)
                .exchange()
                .expectStatus().isNoContent();

        client.get()
                .uri(BASE_URL+"/"+MESSAGE_ID)
                .exchange()
                .expectStatus().isEqualTo(404);

    }

    @Test
    void deleteMessage_notFound_throwsException() {

        client.delete()
                .uri(BASE_URL+"/"+MESSAGE_ID)
                .exchange()
                .expectStatus().isEqualTo(404);

    }

    @Test
    void deleteMessage_malformed_throwsException() {

        client.delete()
                .uri(BASE_URL + "/notALong")
                .exchange()
                .expectStatus().isBadRequest();

    }

}
