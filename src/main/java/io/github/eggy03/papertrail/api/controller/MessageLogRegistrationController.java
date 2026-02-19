package io.github.eggy03.papertrail.api.controller;

import io.github.eggy03.papertrail.api.dto.MessageLogRegistrationDTO;
import io.github.eggy03.papertrail.api.exceptions.handler.ErrorResponse;
import io.github.eggy03.papertrail.api.service.MessageLogRegistrationService;
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
@RequestMapping("/api/v1/log/message")
@Tag(
        name = "Message Log Registration",
        description = "API for registering, fetching, updating, and deleting message log configurations for guilds"
)
public class MessageLogRegistrationController {

    private final MessageLogRegistrationService service;

    @Operation(
            summary = "Register a guild for message logging",
            description = "Creates a new registration for a guild. "
                    + "Throws `GuildAlreadyRegisteredException` if the guild is already registered."
    )

    @ApiResponse(responseCode = "201", description = "Guild registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageLogRegistrationDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Guild already registered", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<MessageLogRegistrationDTO> createRegistration (@RequestBody @Valid MessageLogRegistrationDTO registrationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerGuild(registrationDTO));
    }

    @Operation(
            summary = "Fetch message log registration by guild ID",
            description = "Retrieves the message log registration details of a specific guild. "
                    + "Throws `GuildNotFoundException` if no registration exists."
    )
    @ApiResponse(responseCode = "200", description = "Registration found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageLogRegistrationDTO.class)))
    @ApiResponse(responseCode = "404", description = "Guild not registered", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{guildId}")
    public ResponseEntity<MessageLogRegistrationDTO> findRegistration (@PathVariable @Valid Long guildId) {
        return ResponseEntity.ok(service.findByGuild(guildId));
    }

    @Operation(
            summary = "Update an existing guild registration",
            description = "Updates the channel or other registration details of a guild. "
                    + "Throws `GuildNotFoundException` if the guild is not registered."
    )
    @ApiResponse(responseCode = "200", description = "Registration updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageLogRegistrationDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Guild not registered", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping
    public ResponseEntity<MessageLogRegistrationDTO> updateRegistration (@RequestBody @Valid MessageLogRegistrationDTO updatedRegistrationDTO) {
        return ResponseEntity.ok(service.updateGuild(updatedRegistrationDTO));
    }

    @Operation(
            summary = "Delete a guild's message log registration",
            description = "Removes the message logging registration of a guild. "
                    + "Throws `GuildNotFoundException` if the guild is not registered."
    )
    @ApiResponse(responseCode = "204", description = "Registration deleted successfully")
    @ApiResponse(responseCode = "404", description = "Guild not registered", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{guildId}")
    public ResponseEntity<Void> deleteRegistration (@PathVariable @Valid Long guildId) {
        service.unregisterGuild(guildId);
        return ResponseEntity.noContent().build();
    }
}
