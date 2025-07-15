package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request payload")
public class LoginRequest {
    @Schema(description = "Username of the user", example = "john.doe", required = true)
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "Password of the user", example = "password123", required = true)
    @NotBlank(message = "Password is required")
    private String password;
} 