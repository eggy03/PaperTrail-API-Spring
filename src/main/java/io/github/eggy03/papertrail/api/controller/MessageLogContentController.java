package io.github.eggy03.papertrail.api.controller;

import io.github.eggy03.papertrail.api.dto.MessageLogContentDTO;
import io.github.eggy03.papertrail.api.exceptions.handler.ErrorResponse;
import io.github.eggy03.papertrail.api.service.MessageLogContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/content/message")
@Tag(
        name = "Message Log Content",
        description = "API for registering, fetching, updating, and deleting message logs for guilds"
)
public class MessageLogContentController {

    private final MessageLogContentService service;

    @Operation(
            summary = "Save a new message",
            description = "Logs a message by storing its ID, content, and author. "
                    + "Throws `MessageAlreadyLoggedException` if the message was already logged."
    )
    @ApiResponse(responseCode = "201", description = "Message successfully logged", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageLogContentDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Message has already been logged", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<MessageLogContentDTO> saveMessage(@RequestBody @Valid MessageLogContentDTO message){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveMessage(message));
    }

    @Operation(
            summary = "Fetch a logged message by ID",
            description = "Retrieves the content and author of a logged message by its unique ID. "
                    + "Throws `MessageNotFoundException` if the message does not exist."
    )
    @ApiResponse(responseCode = "200", description = "Message found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageLogContentDTO.class)))
    @ApiResponse(responseCode = "404", description = "Message not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageLogContentDTO> readMessage (@PathVariable @Valid Long messageId) {
        return ResponseEntity.ok(service.findMessageById(messageId));
    }

    @Operation(
            summary = "Update a logged message",
            description = "Updates the content or metadata of a previously logged message. "
                    + "Throws `MessageNotFoundException` if the message does not exist."
    )

    @ApiResponse(responseCode = "200", description = "Message updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageLogContentDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Message not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping
    public ResponseEntity<MessageLogContentDTO> updateMessage (@RequestBody @Valid MessageLogContentDTO updatedMessage) {
        return ResponseEntity.ok(service.updateMessage(updatedMessage));
    }

    @Operation(
            summary = "Delete a logged message",
            description = "Deletes a logged message by ID. "
                    + "Throws `MessageNotFoundException` if the message does not exist."
    )
    @ApiResponse(responseCode = "204", description = "Message deleted successfully")
    @ApiResponse(responseCode = "404", description = "Message not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable @Valid Long messageId) {
        service.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}
