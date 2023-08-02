package com.github.hebra.ram;

import com.github.hebra.ram.model.SignupUser;
import com.github.hebra.ram.model.User;
import com.nulabinc.zxcvbn.Zxcvbn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RamController {

    static final Pattern emailMatcher = Pattern.compile("^(.+)@(\\S+)$");

    final Zxcvbn zxcvbn = new Zxcvbn();
    ;

    final UsersRepository repo;

    @PostMapping(path = "/api/users", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> process(@RequestBody SignupUser signupUser) {

        if (!emailMatcher.matcher(signupUser.email()).matches()) {
            log.error("Invalid email address {}", signupUser.email());
            return ResponseEntity.badRequest().build();
        }

        if (zxcvbn.measure(signupUser.password()).getScore() < 2) {
            log.error("Password score too low.");
            return ResponseEntity.badRequest().build();
        }

        if (repo.findOneByEmail(signupUser.email()).isPresent()) {
            log.warn("User already exists: {}", signupUser.email());
            return ResponseEntity.badRequest().build();
        }

        var userid = Arrays.toString(Base64Coder.encode(UUID.randomUUID().toString().getBytes()));

        var passwordHash = signupUser.password();

        var user = new User(userid, signupUser.email(), passwordHash, signupUser.termsAccepted());
        return ResponseEntity.ok(repo.save(user));
    }


}
