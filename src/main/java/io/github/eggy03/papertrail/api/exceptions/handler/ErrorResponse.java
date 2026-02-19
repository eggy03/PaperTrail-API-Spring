package io.github.eggy03.papertrail.api.exceptions.handler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(name = "ErrorResponse", description = "Represents a standard error response payload")
public class ErrorResponse {

    @Schema(description = "HTTP status code of the error", example = "HTTP Error Code")
    private int status;

    @Schema(description = "Error type / exception", example = "ExampleException")
    private String error;

    @Schema(description = "Detailed error message", example = "ExampleMessage")
    private String message;

    @Schema(description = "Timestamp when the error occurred", example = "2025-08-23T14:32:00Z")
    private LocalDateTime timeStamp;

    @Schema(description = "The path of the request that caused the error", example = "/api/v1/example/")
    private String path;
}
