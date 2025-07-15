package ma.iga.biblio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.iga.biblio.dto.LoginRequest;
import ma.iga.biblio.dto.LoginResponse;
import ma.iga.biblio.dto.RefreshTokenRequest;
import ma.iga.biblio.dto.RegisterRequest;
import ma.iga.biblio.dto.UserDto;
import ma.iga.biblio.entity.RefreshToken;
import ma.iga.biblio.entity.Status;
import ma.iga.biblio.entity.User;
import ma.iga.biblio.security.JwtTokenProvider;
import ma.iga.biblio.service.RefreshTokenService;
import ma.iga.biblio.service.TokenBlacklistService;
import ma.iga.biblio.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    @Operation(summary = "Login user", description = "Authenticates a user and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Account locked or disabled"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Check if user exists and account status before authentication
            User user = userService.findByUsername(loginRequest.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid username or password"));
            }

            // Check account status
            if (user.getStatus() == Status.BLOCKED) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Account is blocked. Please contact administrator."));
            }

            if (user.getStatus() == Status.INACTIVE) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Account is inactive. Please contact administrator."));
            }

            if (user.getStatus() == Status.PENDING) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Account is pending approval. Please wait for administrator approval."));
            }

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            // Delete any existing refresh tokens for this user to avoid unique constraint violation
            refreshTokenService.deleteRefreshTokenByUser(user);
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

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("Invalid username or password"));
        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createErrorResponse("Account is locked. Please contact administrator."));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createErrorResponse("Account is disabled. Please contact administrator."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("An error occurred during login. Please try again."));
        }
    }

    @Operation(summary = "Register new user", description = "Creates a new user account and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration successful",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or username/email already exists"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Check if username or email already exists
            if (userService.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Username already exists"));
            }

            if (userService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Email already exists"));
            }

            User user = userService.createUser(registerRequest);

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    registerRequest.getUsername(),
                    registerRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            // Delete any existing refresh tokens for this user to avoid unique constraint violation
            refreshTokenService.deleteRefreshTokenByUser(user);
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

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Registration failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "Refresh token", description = "Refreshes the JWT token using a refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid refresh token"),
        @ApiResponse(responseCode = "401", description = "Refresh token expired")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            RefreshToken refreshToken = refreshTokenService.verifyExpiration(
                refreshTokenService.findByToken(request.getRefreshToken())
                    .orElseThrow(() -> new RuntimeException("Refresh token not found"))
            );

            User user = refreshToken.getUser();
            
            // Check if user account is still active
            if (user.getStatus() != Status.ACTIVE) {
                refreshTokenService.deleteRefreshTokenByUser(user);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Account is no longer active"));
            }

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

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("Invalid or expired refresh token"));
        }
    }

    @Operation(summary = "Get current user", description = "Returns information about the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User information retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            String username = tokenProvider.getUsernameFromJWT(token);
            User user = userService.findByUsername(username);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not found"));
            }

            UserDto userDto = UserDto.fromEntity(user);

            return ResponseEntity.ok(userDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("Invalid or expired token"));
        }
    }

    @Operation(summary = "Logout user", description = "Invalidates the current JWT token and refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("No valid token provided"));
            }

            String token = authHeader.substring(7); // Remove "Bearer "
            String username = tokenProvider.getUsernameFromJWT(token);
            User user = userService.findByUsername(username);
            
            if (user != null) {
                // Blacklist the JWT token
                tokenBlacklistService.blacklistToken(token);
                
                // Delete the refresh token
                refreshTokenService.deleteRefreshTokenByUser(user);
            }
            
            // Clear the security context
            SecurityContextHolder.clearContext();
            
            return ResponseEntity.ok(createSuccessResponse("Logout successful"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("Invalid or expired token"));
        }
    }

    @Operation(summary = "Logout from all devices", description = "Invalidates all JWT tokens and refresh tokens for the user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout from all devices successful"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("No valid token provided"));
            }

            String token = authHeader.substring(7); // Remove "Bearer "
            String username = tokenProvider.getUsernameFromJWT(token);
            User user = userService.findByUsername(username);
            
            if (user != null) {
                // Delete refresh token for this user
                refreshTokenService.deleteRefreshTokenByUser(user);
                
                // Note: In a more sophisticated system, you might also want to blacklist
                // all current JWT tokens for this user, but that would require tracking
                // all issued tokens per user
            }
            
            // Clear the security context
            SecurityContextHolder.clearContext();
            
            return ResponseEntity.ok(createSuccessResponse("Logout from all devices successful"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("Invalid or expired token"));
        }
    }

    @Operation(summary = "Request password reset", description = "Sends a password reset email to the user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                // For security reasons, we return success even if user doesn't exist
                return ResponseEntity.ok(createSuccessResponse("If an account with this email exists, a password reset link has been sent."));
            }

            // Generate password reset token and send email
            // This would typically involve generating a secure token and sending an email
            // For now, we'll just return a success message
            userService.initiatePasswordReset(user);

            return ResponseEntity.ok(createSuccessResponse("If an account with this email exists, a password reset link has been sent."));

        } catch (Exception e) {
            return ResponseEntity.ok(createSuccessResponse("If an account with this email exists, a password reset link has been sent."));
        }
    }

    @Operation(summary = "Reset password", description = "Resets user password using a reset token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successful"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired reset token")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            boolean success = userService.resetPassword(token, newPassword);
            
            if (success) {
                return ResponseEntity.ok(createSuccessResponse("Password reset successful"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid or expired reset token"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Password reset failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "Validate token", description = "Validates if a JWT token is still valid")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token is valid"),
        @ApiResponse(responseCode = "401", description = "Token is invalid or expired")
    })
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            
            if (tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromJWT(token);
                User user = userService.findByUsername(username);
                
                if (user != null && user.getStatus() == Status.ACTIVE) {
                    return ResponseEntity.ok(createSuccessResponse("Token is valid"));
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("Token is invalid or expired"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("Token is invalid or expired"));
        }
    }

    // Helper methods
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        response.put("success", "false");
        return response;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("success", "true");
        return response;
    }
} 