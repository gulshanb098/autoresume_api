package com.autoresume.autoresume_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileTypeNotAllowedException extends RuntimeException {
    public FileTypeNotAllowedException(String message) {
        super(message);
    }
}
