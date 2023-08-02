package com.github.hebra.ram.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", indexes = {
        @Index(columnList = "email", name = "user_email_hidx"),
})
@NoArgsConstructor
@AllArgsConstructor
@Data
public final class User {
    @Id
    private String userid;
    private String email;
    @JsonIgnore
    private String password_hash;
    @JsonProperty("terms_accepted")
    private boolean termsAccepted;
}
