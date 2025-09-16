package com.autoresume.autoresume_api.service;

import com.autoresume.autoresume_api.model.User;
import com.autoresume.autoresume_api.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("OAuth2 User Info: " + oAuth2User.getAttributes());

        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        userRepository.findByGoogleId(googleId).orElseGet(() -> {
            User newUser = User.builder()
                    .googleId(googleId)
                    .email(email)
                    .name(name)
                    .picture(picture)
                    .createdAt(Instant.now())
                    .build();
            return userRepository.save(newUser);
        });

        // You can redirect to frontend after successful login
        response.sendRedirect("http://localhost:3000/dashboard"); // TODO: update this to your frontend route
    }
}
