package com.thien.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thien.jobseeker.domain.LoginDTO;
import com.thien.jobseeker.domain.User;
import com.thien.jobseeker.domain.response.ResLoginDTO;
import com.thien.jobseeker.service.UserService;
import com.thien.jobseeker.util.SecurityUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDto) {
 
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String access_token = this.securityUtil.createToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        User currentUserInDB = this.userService.handleGetUserByUsername(loginDto.getUsername());
        if (currentUserInDB == null) {
            return ResponseEntity.badRequest().build();
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserInDB.getId(), currentUserInDB.getEmail(), currentUserInDB.getName());
        res.setUser(userLogin);

        res.setAccessToken(access_token);
        return ResponseEntity.ok().body(res);
    }
}