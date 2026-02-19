package io.github.eggy03.papertrail.api.exceptions;

public class MessageNotFoundException extends RuntimeException{

    public  MessageNotFoundException (String message) {
        super(message);
    }
}
