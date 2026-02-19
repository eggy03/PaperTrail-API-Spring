package io.github.eggy03.papertrail.api.exceptions;

public class GuildNotFoundException extends RuntimeException{

    public GuildNotFoundException(String message) {
        super(message);
    }
}
