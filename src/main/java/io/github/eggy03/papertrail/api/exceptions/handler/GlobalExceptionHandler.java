package io.github.eggy03.papertrail.api.exceptions.handler;

import io.github.eggy03.papertrail.api.exceptions.GuildAlreadyRegisteredException;
import io.github.eggy03.papertrail.api.exceptions.GuildNotFoundException;
import io.github.eggy03.papertrail.api.exceptions.MessageAlreadyLoggedException;
import io.github.eggy03.papertrail.api.exceptions.MessageNotFoundException;
import io.github.eggy03.papertrail.api.util.AnsiColor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Optional;

@SuppressWarnings("LoggingSimilarMessage")
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GuildNotFoundException.class)
    public ResponseEntity<ErrorResponse> informGuildNotFound (GuildNotFoundException e, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        log.info(AnsiColor.MAGENTA + "{}" + AnsiColor.RESET, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<ErrorResponse> informMessageNotFound (MessageNotFoundException e, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        log.info(AnsiColor.MAGENTA + "{}" + AnsiColor.RESET, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(GuildAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> informGuildAlreadyRegistered (GuildAlreadyRegisteredException e, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        log.info(AnsiColor.MAGENTA + "{}" + AnsiColor.RESET, e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MessageAlreadyLoggedException.class)
    public ResponseEntity<ErrorResponse> informMessageAlreadyLogged (MessageAlreadyLoggedException e, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        log.info(AnsiColor.MAGENTA + "{}" + AnsiColor.RESET, e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> informMethodArgumentInvalid (MethodArgumentNotValidException e, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                e.getStatusCode().value(),
                e.getClass().getSimpleName(),
                Optional.ofNullable(e.getFieldError())
                        .map(FieldError::getDefaultMessage)
                        .orElse("Generic Validation Error / Validation Message Not Found"),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        log.warn(AnsiColor.YELLOW+"Input validation failed"+AnsiColor.RESET, e);
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> informMethodArgumentInvalid (MethodArgumentTypeMismatchException e, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        log.warn(AnsiColor.YELLOW+"Input validation failed"+AnsiColor.RESET, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        log.error(AnsiColor.RED+"An error has occurred"+AnsiColor.RESET, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
