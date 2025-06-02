package ma.iga.biblio.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String refreshToken;
    private String username;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
} 