package com.github.hebra.ram.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public final class User {
    private String userid;
    private String email;
    @JsonIgnore
    private String password_hash;
    @JsonProperty("terms_accepted")
    private boolean termsAccepted;
}
