package com.github.hebra.exception;

public class InvalidEmailException extends RuntimeException {
    private static final long serialVersionUID = 12L;

    public InvalidEmailException(String message) {
        super(message);
    }
}
