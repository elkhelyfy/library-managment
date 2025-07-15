package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Authentication response payload")
public class LoginResponse {
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Refresh token for getting new access tokens", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;

    @Schema(description = "Username of the authenticated user", example = "john.doe")
    private String username;

    @Schema(description = "Email of the authenticated user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Role of the authenticated user", example = "ROLE_MEMBER")
    private String role;

    @Schema(description = "First name of the authenticated user", example = "John")
    private String firstName;

    @Schema(description = "Last name of the authenticated user", example = "Doe")
    private String lastName;
} 