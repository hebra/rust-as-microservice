package com.github.hebra.repository;

import com.github.hebra.model.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findOneByEmail(String email);

    @Modifying
    @Query("INSERT INTO users (userid, email, password_hash, terms_accepted) VALUES ( " +
            " :userid, :email, :passwordHash, :termsAccepted)")
    int createUser(@Param("userid") String userid,
                   @Param("email") String email,
                   @Param("passwordHash") String passwordHash,
                   @Param("termsAccepted") boolean termsAccepted);
}
