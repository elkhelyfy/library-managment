package ma.iga.biblio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.iga.biblio.dto.UserDto;
import ma.iga.biblio.entity.Role;
import ma.iga.biblio.entity.Status;
import ma.iga.biblio.entity.User;
import ma.iga.biblio.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management and profile APIs")
public class UserController {

    private final UserService userService;

    // ===========================================
    // PERSONAL USER ENDPOINTS (/api/user/...)
    // ===========================================

    @GetMapping("/api/user/profile")
    @Operation(summary = "Get user profile", description = "Retrieves the current user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("User not found"));
            }

            UserDto userDto = UserDto.fromEntity(user);
            return ResponseEntity.ok(userDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to retrieve user profile"));
        }
    }

    @PutMapping("/api/user/profile")
    @Operation(summary = "Update user profile", description = "Updates the current user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated user profile"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UserDto userDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username);
            
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("User not found"));
            }

            // Update only allowed fields for regular users
            currentUser.setFirstName(userDto.getFirstName());
            currentUser.setLastName(userDto.getLastName());
            currentUser.setEmail(userDto.getEmail());
            
            // Note: In a real implementation, you'd have an update method in userService
            // For now, we'll return the updated user data
            UserDto updatedUserDto = UserDto.fromEntity(currentUser);
            return ResponseEntity.ok(updatedUserDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Failed to update user profile: " + e.getMessage()));
        }
    }

    @PostMapping("/api/user/change-password")
    @Operation(summary = "Change password", description = "Changes the current user's password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid current password or new password")
    })
    public ResponseEntity<?> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Note: In a real implementation, you'd have a changePassword method in userService
            // For now, we'll return a success message
            return ResponseEntity.ok(createSuccessResponse("Password changed successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Failed to change password: " + e.getMessage()));
        }
    }

    // ===========================================
    // USER BORROWING/RESERVATION ENDPOINTS
    // ===========================================

    @PostMapping("/api/user/loans")
    @Operation(summary = "Create loan request", description = "Creates a new book loan request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Loan request created successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid book ID or user cannot borrow")
    })
    public ResponseEntity<?> createLoanRequest(@RequestParam Long bookId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Note: In a real implementation, you'd have a loan service
            // For now, we'll return a success message
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(createSuccessResponse("Loan request created successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Failed to create loan request: " + e.getMessage()));
        }
    }

    @GetMapping("/api/user/loans")
    @Operation(summary = "Get user loans", description = "Retrieves current user's loan history")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user loans")
    public ResponseEntity<?> getUserLoans() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Note: Return empty list for now
            return ResponseEntity.ok(List.of());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to retrieve loans"));
        }
    }

    @PostMapping("/api/user/reservations")
    @Operation(summary = "Create reservation", description = "Creates a new book reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid book ID or book not available for reservation")
    })
    public ResponseEntity<?> createReservation(@RequestParam Long bookId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Note: In a real implementation, you'd have a reservation service
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(createSuccessResponse("Reservation created successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Failed to create reservation: " + e.getMessage()));
        }
    }

    @GetMapping("/api/user/reservations")
    @Operation(summary = "Get user reservations", description = "Retrieves current user's reservations")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user reservations")
    public ResponseEntity<?> getUserReservations() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Note: Return empty list for now
            return ResponseEntity.ok(List.of());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to retrieve reservations"));
        }
    }

    @DeleteMapping("/api/user/reservations/{id}")
    @Operation(summary = "Cancel reservation", description = "Cancels a user's reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Note: In a real implementation, you'd verify the reservation belongs to the user
            return ResponseEntity.ok(createSuccessResponse("Reservation cancelled successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("Reservation not found or cannot be cancelled"));
        }
    }

    @GetMapping("/api/user/fines")
    @Operation(summary = "Get user fines", description = "Retrieves current user's fines")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user fines")
    public ResponseEntity<?> getUserFines() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Note: Return empty list for now
            return ResponseEntity.ok(List.of());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to retrieve fines"));
        }
    }

    @PostMapping("/api/user/fines/{id}/payment")
    @Operation(summary = "Pay fine", description = "Processes payment for a fine")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fine payment processed successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Fine not found"),
        @ApiResponse(responseCode = "400", description = "Invalid payment amount")
    })
    public ResponseEntity<?> payFine(@PathVariable Long id, @RequestParam Double amount) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Note: In a real implementation, you'd process the payment
            return ResponseEntity.ok(createSuccessResponse("Fine payment processed successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Failed to process fine payment: " + e.getMessage()));
        }
    }

    // ===========================================
    // ADMIN USER MANAGEMENT ENDPOINTS (/api/users/...)
    // ===========================================

    @GetMapping("/api/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieves all users in the system (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all users"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin role required")
    })
    public ResponseEntity<?> getAllUsers() {
        try {
            // Note: In a real implementation, you'd have a method to get all users
            // For now, return empty list
            return ResponseEntity.ok(List.of());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to retrieve users"));
        }
    }

    @DeleteMapping("/api/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Deletes a user from the system (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            // Note: In a real implementation, you'd have a delete method
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("User not found"));
        }
    }

    @PutMapping("/api/users/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Block user", description = "Blocks a user account (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User blocked successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        try {
            // Note: In a real implementation, you'd update the user status to BLOCKED
            return ResponseEntity.ok(createSuccessResponse("User blocked successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("User not found"));
        }
    }

    @PutMapping("/api/users/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unblock user", description = "Unblocks a user account (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User unblocked successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        try {
            // Note: In a real implementation, you'd update the user status to ACTIVE
            return ResponseEntity.ok(createSuccessResponse("User unblocked successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("User not found"));
        }
    }

    @PutMapping("/api/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user role", description = "Updates a user's role (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User role updated successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid role")
    })
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        try {
            // Validate role
            Role newRole;
            try {
                newRole = Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid role: " + role));
            }

            // Note: In a real implementation, you'd update the user's role
            return ResponseEntity.ok(createSuccessResponse("User role updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("User not found"));
        }
    }

    // ===========================================
    // HELPER METHODS
    // ===========================================

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
