package com.miva.manager.bean;

import com.miva.manager.exception.MessageManagerException;
import lombok.Data;

@Data
public class ErrorMessageJson {

    private final int status;
    private final int errorId;
    private final String errorMessage;
    private final String details;

    public ErrorMessageJson(MessageManagerException ex, String details) {
        this.status = ex.getStatusCode();
        this.errorId = ex.getId();
        this.errorMessage = ex.getMessage();
        this.details = details;
    }
}
