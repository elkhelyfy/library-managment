package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Refresh token request payload")
public class RefreshTokenRequest {
    @Schema(description = "Refresh token to get new access token", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    @NotBlank
    private String refreshToken;
} 