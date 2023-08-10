package com.github.hebra.exception;

public class UserWithEmailException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserWithEmailException(String message) {
        super(message);
    }
}