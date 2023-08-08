package com.github.hebra.ram;

import com.github.hebra.ram.model.SignupUser;
import com.github.hebra.ram.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RamController {

    static final Pattern emailMatcher = Pattern.compile("^(.+)@(\\S+)$");

    final Connection db;

//    final Zxcvbn zxcvbn = new Zxcvbn();

    @PostMapping(path = "/api/users", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> process(@RequestBody SignupUser signupUser) {

        if (!emailMatcher.matcher(signupUser.email()).matches()) {
            log.error("Invalid email address {}", signupUser.email());
            return ResponseEntity.badRequest().build();
        }


//        if (zxcvbn.measure(signupUser.password()).getScore() < 2) {
//            log.error("Password score too low.");
//            return ResponseEntity.badRequest().build();
//        }

        if (userExists(signupUser.email())) {
            log.warn("User already exists: {}", signupUser.email());
            return ResponseEntity.badRequest().build();
        }

        var userid = Arrays.toString(Base64Coder.encode(UUID.randomUUID().toString().getBytes()));

        var passwordHash = signupUser.password();

        var user = new User(userid, signupUser.email(), passwordHash, signupUser.termsAccepted());

        insertUser(user);

        return ResponseEntity.ok(user);
    }


    private boolean userExists(String email) {
        var sql = "SELECT count(1) as count FROM users WHERE email=?";

        try (var query = db.prepareStatement(sql)) {
            query.setString(1, email);
            var count = query.executeQuery();
            return Long.parseLong(count.getString(1)) > 0;
        } catch (SQLException | NumberFormatException ex) {
            log.error(ex.getMessage());
        }

        return false;
    }

    private void insertUser(User user) {
        var sql = "INSERT INTO users(userid, email, password_hash, terms_accepted) VALUES (?, ?, ?, ?)";

        try (var query = db.prepareStatement(sql)) {
            query.setString(1, user.getUserid());
            query.setString(2, user.getEmail());
            query.setString(3, user.getPassword_hash());
            query.setBoolean(4, user.isTermsAccepted());

            query.executeUpdate();
        } catch (SQLException | NumberFormatException ex) {
            log.error(ex.getMessage());
        }

    }
}
