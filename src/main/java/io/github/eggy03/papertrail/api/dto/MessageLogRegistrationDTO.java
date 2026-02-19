package io.github.eggy03.papertrail.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "MessageLogRegistration", description = "Represents the registration of a guild for message logging")
public class MessageLogRegistrationDTO {

    @NotNull(message = "GuildID cannot be null")
    @Schema(
            description = "Unique identifier of the guild",
            example = "123456789012345678",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long guildId;

    @NotNull(message = "ChannelID cannot be null")
    @Schema(
            description = "Identifier of the channel where messages will be logged",
            example = "987654321098765432",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long channelId;

}
