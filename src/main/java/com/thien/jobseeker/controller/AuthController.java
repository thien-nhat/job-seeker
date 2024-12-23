package com.thien.jobseeker.controller;

import com.thien.jobseeker.util.error.IdInvalidException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.thien.jobseeker.domain.LoginDTO;
import com.thien.jobseeker.domain.User;
import com.thien.jobseeker.domain.response.ResLoginDTO;
import com.thien.jobseeker.service.UserService;
import com.thien.jobseeker.util.SecurityUtil;
import com.thien.jobseeker.util.annotation.ResponseMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDto) {
        logger.info("Executing login method");

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        User currentUserInDB = this.userService.handleGetUserByUsername(loginDto.getUsername());
        if (currentUserInDB == null) {
            return ResponseEntity.badRequest().build();
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserInDB.getId(), currentUserInDB.getEmail(),
                currentUserInDB.getName());
        res.setUser(userLogin);

        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());
        res.setAccessToken(access_token);

        String refreshToken = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);
        this.userService.updateUserToken(refreshToken, loginDto.getUsername());

        ResponseCookie cookie = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    @ResponseMessage("fetch account successfully")
    public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUserInDB = this.userService.handleGetUserByUsername(email);
        if (currentUserInDB == null) {
            return ResponseEntity.badRequest().build();
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUserInDB.getId(), currentUserInDB.getEmail(), currentUserInDB.getName());

        return ResponseEntity.ok().body(userLogin);
    }

    @GetMapping("auth/refresh")
    @ResponseMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token") String refreshToken
    ) throws IdInvalidException {
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh token not valid");
        }

        ResLoginDTO res = new ResLoginDTO();
        User currentUserInDB = this.userService.handleGetUserByUsername(email);
        if (currentUserInDB == null) {
            return ResponseEntity.badRequest().build();
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserInDB.getId(), currentUserInDB.getEmail(),
                currentUserInDB.getName());
        res.setUser(userLogin);

        String access_token = this.securityUtil.createAccessToken(email, res.getUser());
        res.setAccessToken(access_token);

        String new_refreshToken = this.securityUtil.createRefreshToken(email, res);
        this.userService.updateUserToken(new_refreshToken, email);

        ResponseCookie cookie = ResponseCookie
                .from("refresh_token", new_refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }
}