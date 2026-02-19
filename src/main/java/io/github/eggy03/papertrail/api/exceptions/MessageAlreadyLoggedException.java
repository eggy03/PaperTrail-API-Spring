package io.github.eggy03.papertrail.api.exceptions;

public class MessageAlreadyLoggedException extends RuntimeException {

    public MessageAlreadyLoggedException(String message) {
        super(message);
    }
}
