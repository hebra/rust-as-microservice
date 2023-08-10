package com.github.hebra.exception;

public class LowScorePasswordException extends RuntimeException {
    private static final long serialVersionUID = 13L;

    public LowScorePasswordException(String message) {
        super(message);
    }
}
