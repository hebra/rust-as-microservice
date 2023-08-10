package com.github.hebra.controller;

import com.github.hebra.exception.InvalidEmailException;
import com.github.hebra.exception.LowScorePasswordException;
import com.github.hebra.exception.UserWithEmailException;
import com.github.hebra.model.ExceptionInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    protected static final String ERROR_LOG_MESSAGE_TEMPLATE = "{}: {}";


    @ExceptionHandler({
            UserWithEmailException.class,
            LowScorePasswordException.class,
            InvalidEmailException.class
    })
    public ResponseEntity<ExceptionInfoResponse> handleRequestsException(HttpServletRequest request,
                                                                         Exception exception) {
        log.info(ERROR_LOG_MESSAGE_TEMPLATE, BAD_REQUEST, exception.getMessage());
        return newResponseEntity(BAD_REQUEST, request, exception.getMessage());
    }

    @ExceptionHandler({
            Exception.class,
    })
    public ResponseEntity<ExceptionInfoResponse> handleIllegalArgument(HttpServletRequest request,
                                                                       Exception exception) {
        log.info(ERROR_LOG_MESSAGE_TEMPLATE, BAD_REQUEST, exception.getMessage());
        return newResponseEntity(BAD_REQUEST, request, exception.getMessage());
    }

    protected ResponseEntity<ExceptionInfoResponse> newResponseEntity(HttpStatus status,
                                                                      HttpServletRequest request,
                                                                      String exceptionMessage) {
        return ResponseEntity.status(status)
                .body(ExceptionInfoResponse.builder()
                        .path(request.getRequestURI())
                        .status(status)
                        .message(exceptionMessage)
                        .build());
    }

}
