package com.github.hebra.controller;

import com.github.hebra.exception.InvalidEmailException;
import com.github.hebra.exception.LowScorePasswordException;
import com.github.hebra.model.SignupUser;
import com.github.hebra.model.User;
import com.github.hebra.service.UserService;
import com.nulabinc.zxcvbn.Zxcvbn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class UsersController {

    static final Pattern emailMatcher = Pattern.compile("^(.+)@(\\S+)$");


    final Zxcvbn zxcvbn = new Zxcvbn();

    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/api/users", consumes = APPLICATION_JSON_VALUE)
    public Mono<User> process (@RequestBody SignupUser signupUser) {

        if (!emailMatcher.matcher(signupUser.email()).matches()) {
            log.error("Invalid email address {}", signupUser.email());
            return Mono.error(new InvalidEmailException("Invalid email address " + signupUser.email()));
        }

        if (zxcvbn.measure(signupUser.password()).getScore() < 2) {
            log.error("Password score too low.");
            return Mono.error(new LowScorePasswordException("Password score too low."));
        }

        return userService.saveUser(signupUser);
    }

//    @PostMapping(path = "/api/users", consumes = APPLICATION_JSON_VALUE)
//    public Mono<User> process(@RequestBody SignupUser signupUser) {
//
//        if (!emailMatcher.matcher(signupUser.email()).matches()) {
//            log.error("Invalid email address {}", signupUser.email());
//            return ResponseEntity.badRequest().build();
//        }
//
//        if (zxcvbn.measure(signupUser.password()).getScore() < 2) {
//            log.error("Password score too low.");
//            return ResponseEntity.badRequest().build();
//        }
//
//        if (repo.findOneByEmail(signupUser.email()).isPresent()) {
//            log.warn("User already exists: {}", signupUser.email());
//            return ResponseEntity.badRequest().build();
//        }
//
//        var userid = Arrays.toString(Base64Coder.encode(UUID.randomUUID().toString().getBytes()));
//
//        var passwordHash = signupUser.password();
//
//        var user = new User(userid, signupUser.email(), passwordHash, signupUser.termsAccepted());
//        return ResponseEntity.ok(repo.save(user));
//    }

//    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiResponse(responseCode = "200", description = "Return all delivery branches")
//    public Mono<DeliveryBranchBasicInfoListDto> listDeliveryBranches() {
//        return deliveryBranchService.listDeliveryBranches();
//    }
//
//    // Retrieves a delivery branch by branchCode
//    @GetMapping(value = "/{branchCode}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiResponse(responseCode = "200", description = "Return Delivery Branch Info for a branch code")
//    @ApiResponse(responseCode = "404", description = "Delivery Branch not found")
//    public Mono<DeliveryBranchInfoDto> getDeliveryBranch(@PathVariable String branchCode,
//                                                         @RequestParam(value = "includeLinkedBranches", required = false) boolean includeLinkedBranches) {
//        return deliveryBranchService.getDeliveryBranch(branchCode, includeLinkedBranches);
//    }
//
//    // Retrieves linked branches by branchCode
//    @GetMapping(value = "/{branchCode}/linked-branches", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiResponse(responseCode = "200", description = "Return Linked branches for a branch code")
//    public Mono<LinkedBranchesWrapperDto> getLinkedBranches(@PathVariable String branchCode) {
//        return deliveryBranchService.getLinkedBranches(branchCode);
//    }
//
//    // add/update a delivery branch by branchCode
//    @PutMapping(value = "/{branchCode}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiResponse(responseCode = "200", description = "add/update a delivery branch by branchCode")
//    public Mono<DeliveryBranchInfoDto> putDeliveryBranch(@PathVariable String branchCode, @RequestBody DeliveryBranchInfoDto deliveryBranchInfoDto) {
//        log.info("about to create/update for branch {}", branchCode);
//        Optional<IllegalArgumentException> validationError = branchControllerValidator.validateForCreateUpdateChannel(deliveryBranchInfoDto);
//        return validationError.<Mono<DeliveryBranchInfoDto>>map(Mono::error)
//                .orElseGet(() -> deliveryBranchService.saveDeliveryBranch(deliveryBranchInfoDto));
//    }
}
