package com.thien.jobseeker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thien.jobseeker.domain.User;
import com.thien.jobseeker.domain.response.ResCreateUserDTO;
import com.thien.jobseeker.domain.response.ResUserDTO;
import com.thien.jobseeker.domain.response.ResultPaginationDTO;
import com.thien.jobseeker.service.UserService;
import com.thien.jobseeker.util.annotation.ResponseMessage;
import com.thien.jobseeker.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ResponseMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User postManUser) throws IdInvalidException {
        logger.info("Executing createNewUser method");

        boolean isEmailExist = this.userService.isEmailExit(postManUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email" + postManUser.getEmail() + " da ton tai");
        }


        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User user = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    @ResponseMessage("Delete an user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {
        logger.info("Executing deleteUser method");
                
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User voi Id = " + id +  " khong ton tai");
            
        }


        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok().build();
    }

    // fetch user by id
    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        logger.info("Executing getUserById method");

        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("User voi Id = " + id +  " khong ton tai");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(fetchUser));
    }

    // fetch all users
    @GetMapping("/users")
    @ResponseMessage("Get all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {
        logger.info("Executing getAllUser method");

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
    }

    @PutMapping("/users")
    public ResponseEntity<ResUserDTO> updateUser(@RequestBody User user) {
        logger.info("Executing updateUser method");
        
        User updateUser = this.userService.handleUpdateUser(user);
        return ResponseEntity.ok(this.userService.convertToResUserDTO(updateUser));
    }

}
