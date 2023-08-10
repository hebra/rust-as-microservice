package com.github.hebra.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignupUser(
        String email,
        String password,
        @JsonProperty("terms_accepted")
        boolean termsAccepted
) {
}

