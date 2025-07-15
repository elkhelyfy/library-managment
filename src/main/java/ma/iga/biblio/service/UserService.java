package ma.iga.biblio.service;

import ma.iga.biblio.dto.RegisterRequest;
import ma.iga.biblio.dto.UserDto;
import ma.iga.biblio.entity.User;

import java.util.Optional;

public interface UserService {
    User createUser(RegisterRequest registerRequest);
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    UserDto createUser(UserDto userDto);
    Optional<User> getUserById(Long id);
    void initiatePasswordReset(User user);
    boolean resetPassword(String token, String newPassword);
} 