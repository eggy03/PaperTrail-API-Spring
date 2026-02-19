package io.github.eggy03.papertrail.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "AuditLogRegistration", description = "Represents the registration of a guild for audit logging")
public class AuditLogRegistrationDTO {

    @NotNull(message = "GuildID cannot be null")
    @Schema(
            description = "Unique identifier of the guild",
            example = "987654321234567890",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long guildId;

    @NotNull(message = "ChannelID cannot be null")
    @Schema(
            description = "Unique identifier of the channel where audit logs will be sent",
            example = "123456789012345678",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long channelId;
}