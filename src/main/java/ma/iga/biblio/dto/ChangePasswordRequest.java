package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Change password request data transfer object")
public class ChangePasswordRequest {
    @Schema(description = "Current password", required = true)
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Schema(description = "New password", required = true)
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    @Schema(description = "Confirm new password", required = true)
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
} 