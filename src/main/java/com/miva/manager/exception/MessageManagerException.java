package com.miva.manager.exception;

import lombok.Getter;
import lombok.ToString;

import java.util.Random;

@Getter
@ToString
public class MessageManagerException extends RuntimeException {

    private static final Random RANDOM = new Random();

    private final int id = RANDOM.nextInt(Integer.MAX_VALUE);

    private final int statusCode;

    public MessageManagerException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public MessageManagerException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getId() {
        return id;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getLogMessage() {
        return String.format("id=%1$s, statusCode=%2$s, detailMessage=%3$s", getId(), getStatusCode(), getMessage());
    }
}
