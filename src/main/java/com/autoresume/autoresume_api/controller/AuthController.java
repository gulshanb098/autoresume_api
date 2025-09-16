package com.autoresume.autoresume_api.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> user(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getClaims(); // includes email, name, sub, etc.
    }
}
