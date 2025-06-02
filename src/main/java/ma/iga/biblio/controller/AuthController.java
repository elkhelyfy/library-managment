package ma.iga.biblio.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.iga.biblio.dto.LoginRequest;
import ma.iga.biblio.dto.LoginResponse;
import ma.iga.biblio.dto.RefreshTokenRequest;
import ma.iga.biblio.dto.RegisterRequest;
import ma.iga.biblio.entity.RefreshToken;
import ma.iga.biblio.entity.User;
import ma.iga.biblio.security.JwtTokenProvider;
import ma.iga.biblio.service.RefreshTokenService;
import ma.iga.biblio.service.TokenBlacklistService;
import ma.iga.biblio.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        User user = (User) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        
        LoginResponse response = LoginResponse.builder()
            .token(jwt)
            .refreshToken(refreshToken.getToken())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().name())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = userService.createUser(registerRequest);

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                registerRequest.getUsername(),
                registerRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        LoginResponse response = LoginResponse.builder()
            .token(jwt)
            .refreshToken(refreshToken.getToken())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().name())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyExpiration(
            refreshTokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"))
        );

        User user = refreshToken.getUser();
        String token = tokenProvider.generateToken(user.getUsername());

        LoginResponse response = LoginResponse.builder()
            .token(token)
            .refreshToken(refreshToken.getToken())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().name())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer "
        String username = tokenProvider.getUsernameFromJWT(token);
        User user = userService.findByUsername(username);
        
        // Blacklist the JWT token
        tokenBlacklistService.blacklistToken(token);
        
        // Delete the refresh token
        refreshTokenService.deleteRefreshTokenByUser(user);
        
        // Clear the security context
        SecurityContextHolder.clearContext();
        
        return ResponseEntity.ok().build();
    }
} 