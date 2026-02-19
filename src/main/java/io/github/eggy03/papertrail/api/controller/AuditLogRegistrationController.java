package io.github.eggy03.papertrail.api.controller;

import io.github.eggy03.papertrail.api.dto.AuditLogRegistrationDTO;
import io.github.eggy03.papertrail.api.exceptions.handler.ErrorResponse;
import io.github.eggy03.papertrail.api.service.AuditLogRegistrationService;
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
@RequestMapping("/api/v1/log/audit")
@Tag(
        name = "Audit Log Registration",
        description = "API for registering, fetching, updating, and deleting audit log configurations for guilds"
)
public class AuditLogRegistrationController {

    private final AuditLogRegistrationService service;

    @Operation(
            summary = "Register a new guild for audit logging",
            description = "Creates a new audit log registration by mapping a guild ID to a channel ID. "
                    + "If the guild is already registered, a `GuildAlreadyRegisteredException` is thrown."
    )
    @ApiResponse(responseCode = "201", description = "Guild successfully registered", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLogRegistrationDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Guild already registered for audit logging", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<AuditLogRegistrationDTO> createRegistration (@RequestBody @Valid AuditLogRegistrationDTO registrationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerGuild(registrationDTO));
    }

    @Operation(
            summary = "Get audit log registration for a guild",
            description = "Retrieves the audit log registration details (guild ID and channel ID) for a given guild."
    )
    @ApiResponse(responseCode = "200", description = "Guild registration found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLogRegistrationDTO.class)))
    @ApiResponse(responseCode = "404", description = "Guild not registered for audit logging", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{guildId}")
    public ResponseEntity<AuditLogRegistrationDTO> findRegistration (@PathVariable @Valid Long guildId) {
        return ResponseEntity.ok(service.findByGuild(guildId));
    }

    @Operation(
            summary = "Update an existing audit log registration",
            description = "Updates the channel mapping for an already-registered guild. "
                    + "If the guild does not exist, a `GuildNotFoundException` is thrown."
    )
    @ApiResponse(responseCode = "200", description = "Guild registration successfully updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLogRegistrationDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Guild not registered for audit logging", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping
    public ResponseEntity<AuditLogRegistrationDTO> updateRegistration (@RequestBody @Valid AuditLogRegistrationDTO updatedDTO) {
        return ResponseEntity.ok(service.updateGuild(updatedDTO));
    }

    @Operation(
            summary = "Delete an audit log registration",
            description = "Unregisters a guild from audit logging. "
                    + "If the guild does not exist, a `GuildNotFoundException` is thrown."
    )
    @ApiResponse(responseCode = "204", description = "Guild successfully unregistered")
    @ApiResponse(responseCode = "404", description = "Guild not registered for audit logging", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{guildId}")
    public ResponseEntity<Void> deleteRegistration (@PathVariable @Valid Long guildId) {
        service.unregisterGuild(guildId);
        return ResponseEntity.noContent().build();
    }
}
