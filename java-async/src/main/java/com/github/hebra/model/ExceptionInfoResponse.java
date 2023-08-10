package com.github.hebra.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Value
public class ExceptionInfoResponse {

    ZonedDateTime timestamp = ZonedDateTime.now();
    String path; // url path
    int status;  // HTTP error status
    String error; // HTTP error reason phrase
    String message; // error message
    Object detail; // error detail

    @Builder
    private ExceptionInfoResponse(String path, HttpStatus status, String message, Object detail) {
        this.path = path;
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.detail = detail;
    }
}
