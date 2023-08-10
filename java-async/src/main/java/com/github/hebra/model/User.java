package com.github.hebra.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public final class User {
    @Id
    private String userid;
    private String email;
    @JsonIgnore
    private String password_hash;
    @JsonProperty("terms_accepted")
    private boolean terms_accepted;
}
