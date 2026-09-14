package com.blog.service;

import com.blog.dto.LoginRequest;
import com.blog.dto.LoginResponse;
import com.blog.dto.SignupRequest;
import com.blog.dto.SignupResponse;
import com.blog.entity.User;
import com.blog.repository.UserRepository;
import com.blog.security.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    @Value("${google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${google.token-uri}")
    private String googleTokenUri;

    private final RestTemplate restTemplate = new RestTemplate();

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        User saved = userRepository.save(user);
        return new SignupResponse(saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getUsername());
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(token, user.getFirstName(), user.getId());
    }

    @SuppressWarnings("unchecked")
    public LoginResponse googleLogin(String code, String codeVerifier) throws Exception {
        // Exchange authorization code for access token
        String tokenUrl = googleTokenUri + "?code=" + code +
                "&client_id=" + googleClientId +
                "&client_secret=" + googleClientSecret +
                "&redirect_uri=" + googleRedirectUri +
                "&grant_type=authorization_code" +
                "&code_verifier=" + codeVerifier;

        Map<String, Object> tokenResponse = restTemplate.postForObject(tokenUrl, null, Map.class);
        String accessToken = (String) tokenResponse.get("access_token");

        // Get user info from Google
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;
        Map<String, Object> userInfo = restTemplate.getForObject(userInfoUrl, Map.class);

        String email = (String) userInfo.get("email");
        String firstName = (String) userInfo.get("given_name");
        String lastName = (String) userInfo.get("family_name");

        // Find or create user
        User user = userRepository.findByUsername(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .firstName(firstName)
                            .lastName(lastName)
                            .username(email)
                            .password(passwordEncoder.encode("")) // No password for OAuth users
                            .build();
                    return userRepository.save(newUser);
                });

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(token, user.getFirstName(), user.getId());
    }
}
