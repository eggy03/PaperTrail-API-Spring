package io.github.eggy03.papertrail.api.exceptions;

public class GuildAlreadyRegisteredException extends RuntimeException{

    public GuildAlreadyRegisteredException(String message) {
        super(message);
    }
}
