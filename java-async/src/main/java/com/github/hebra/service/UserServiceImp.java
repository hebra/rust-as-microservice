package com.github.hebra.service;

import com.github.hebra.exception.UserWithEmailException;
import com.github.hebra.model.SignupUser;
import com.github.hebra.model.User;
import com.github.hebra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Arrays;
import java.util.UUID;

 @Service
 public class UserServiceImp implements UserService {

    private UserRepository userRepository;
    private final Scheduler reactiveScheduler;

    @Autowired
    public UserServiceImp (Scheduler reactiveScheduler, UserRepository userRepository) {
        this.reactiveScheduler = reactiveScheduler;
        this.userRepository = userRepository;
    }

     public Mono<User> saveUser(SignupUser signupUser) {
            return Mono.defer(() -> {
                        if (userRepository.findOneByEmail(signupUser.email()).isPresent()){
                            return Mono.error(new UserWithEmailException("Email is already in use: " + signupUser.email()));
                        }
                        final var newUser = createUserFromSignup(signupUser);
                        userRepository.createUser(newUser.getUserid(), newUser.getEmail(), newUser.getPassword_hash(), newUser.isTerms_accepted());
                        return Mono.just(newUser);
                    })
                    .subscribeOn(reactiveScheduler)
                    .publishOn(reactiveScheduler);
     }

     private User createUserFromSignup(SignupUser signupUser) {
        final var userid = Arrays.toString(Base64Coder.encode(UUID.randomUUID().toString().getBytes()));
        return User.builder().userid(userid).email(signupUser.email()).password_hash(signupUser.password()).terms_accepted(signupUser.termsAccepted()).build();
     }
 }