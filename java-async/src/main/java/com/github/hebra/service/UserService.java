package com.github.hebra.service;

import com.github.hebra.model.SignupUser;
import com.github.hebra.model.User;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> saveUser(SignupUser signupUser);
}