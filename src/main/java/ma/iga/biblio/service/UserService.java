package ma.iga.biblio.service;

import ma.iga.biblio.dto.RegisterRequest;
import ma.iga.biblio.entity.User;

public interface UserService {
    User createUser(RegisterRequest registerRequest);
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
} 