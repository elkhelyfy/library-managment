package ma.iga.biblio.service.impl;

import lombok.RequiredArgsConstructor;
import ma.iga.biblio.dto.RegisterRequest;
import ma.iga.biblio.dto.UserDto;
import ma.iga.biblio.entity.Role;
import ma.iga.biblio.entity.Status;
import ma.iga.biblio.entity.User;
import ma.iga.biblio.repository.UserRepository;
import ma.iga.biblio.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setRole(Role.ROLE_MEMBER);
        user.setStatus(Status.ACTIVE);

        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setRole(userDto.getRole());
        user.setStatus(userDto.getStatus());
        String password = userDto.getPassword() != null ? passwordEncoder.encode(userDto.getPassword()) : "library123";
        user.setPassword(password);
        User savedUser = userRepository.save(user);

        return UserDto.fromEntity(savedUser);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void initiatePasswordReset(User user) {
        // For now, just log the password reset request
        // In a real implementation, you would:
        // 1. Generate a secure reset token
        // 2. Store it in the database with expiration
        // 3. Send an email with the reset link
        System.out.println("Password reset initiated for user: " + user.getEmail());
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        // For now, just return false to indicate the feature is not fully implemented
        // In a real implementation, you would:
        // 1. Find the user by the reset token
        // 2. Check if the token is valid and not expired
        // 3. Update the user's password
        // 4. Delete the reset token
        System.out.println("Password reset attempted with token: " + token);
        return false;
    }

}