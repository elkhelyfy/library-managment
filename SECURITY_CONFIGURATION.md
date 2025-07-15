# Library Management System - Security Configuration

## Overview
This document describes the security configuration for the library management system, which supports both public and private access with role-based authorization.

## Architecture
The system uses JWT (JSON Web Tokens) for stateless authentication with the following components:
- **JWT Token Provider**: Generates and validates JWT tokens
- **JWT Authentication Filter**: Intercepts requests to validate tokens
- **Role-Based Access Control**: Different access levels based on user roles

## User Roles
The system supports five user roles with different privilege levels:

| Role | Description | Privileges |
|------|-------------|------------|
| `ROLE_ADMIN` | Administrator with full system access | All operations including user management, system configuration |
| `ROLE_LIBRARIAN` | Library staff with book management access | Book management, loan/reservation approval, fine management |
| `ROLE_MEMBER` | Regular library member | Basic borrowing, reservations, profile management |
| `ROLE_STUDENT` | Student with extended borrowing privileges | Extended borrowing periods, student-specific features |
| `ROLE_FACULTY` | Faculty member with special privileges | Extended borrowing, priority reservations |

## Access Control Matrix

### Public Access (No Authentication Required)
- **Authentication**: Login, Register, Token Refresh, Logout
- **Book Browsing**: View all books, search books, view book details
- **Category/Author Browsing**: View categories and authors
- **Documentation**: Swagger UI, API docs
- **Health Check**: Application health endpoint

### Authenticated User Access
All authenticated users (regardless of role) can access:
- **Profile Management**: View/update personal profile, change password
- **Borrowing**: Create loan requests, view personal loans
- **Reservations**: Create/view/cancel personal reservations
- **Fines**: View personal fines, make payments

### Librarian & Admin Access
Users with `ROLE_LIBRARIAN` or `ROLE_ADMIN` can:
- **Book Management**: Add, edit, delete books, update availability
- **Loan Management**: Approve/reject loans, process returns, view overdue books
- **Reservation Management**: Approve/reject reservations
- **Fine Management**: Waive fines, manage fine policies

### Admin Only Access
Users with `ROLE_ADMIN` can:
- **User Management**: View all users, delete users, block/unblock users
- **Role Management**: Assign/change user roles
- **System Configuration**: Update fine configuration, system settings

## API Endpoint Security

### Public Endpoints
```
GET  /api/books                    - List all books
GET  /api/books/{id}              - Get book details
GET  /api/books/search            - Search books
GET  /api/books/category/{id}     - Books by category
GET  /api/books/author/{id}       - Books by author
GET  /api/books/{id}/availability - Check availability
GET  /api/categories              - List categories
GET  /api/authors                 - List authors
POST /api/auth/login              - User login
POST /api/auth/register           - User registration
POST /api/auth/refresh            - Token refresh
POST /api/auth/logout             - User logout
```

### Authenticated User Endpoints
```
GET  /api/user/profile            - Get user profile
PUT  /api/user/profile            - Update user profile
POST /api/user/change-password    - Change password
POST /api/user/loans              - Create loan request
GET  /api/user/loans              - Get user loans
POST /api/user/reservations       - Create reservation
GET  /api/user/reservations       - Get user reservations
DELETE /api/user/reservations/{id} - Cancel reservation
GET  /api/user/fines              - Get user fines
POST /api/user/fines/{id}/payment - Pay fine
```

### Librarian/Admin Endpoints
```
POST   /api/books                 - Add new book
PUT    /api/books/{id}            - Update book
DELETE /api/books/{id}            - Delete book
PATCH  /api/books/{id}/availability - Update availability
POST   /api/loans/{id}/approve    - Approve loan
POST   /api/loans/{id}/reject     - Reject loan
POST   /api/loans/{id}/return     - Process return
GET    /api/loans/overdue         - Get overdue loans
POST   /api/reservations/{id}/approve - Approve reservation
POST   /api/reservations/{id}/reject  - Reject reservation
POST   /api/fines/{id}/waive      - Waive fine
```

### Admin Only Endpoints
```
GET    /api/users                 - List all users
DELETE /api/users/{id}            - Delete user
PUT    /api/users/{id}/block      - Block user
PUT    /api/users/{id}/unblock    - Unblock user
PUT    /api/users/{id}/role       - Change user role
PUT    /api/fines/configuration   - Update fine settings
```

## Security Features

### JWT Token Authentication
- **Stateless**: No server-side session storage
- **Secure**: Tokens are signed and validated
- **Refresh Tokens**: Long-lived tokens for token renewal
- **Token Blacklisting**: Invalidated tokens are blacklisted

### Password Security
- **BCrypt Hashing**: Strong password hashing algorithm
- **Password Policies**: Can be enforced through validation

### CORS and CSRF
- **CORS**: Cross-Origin Resource Sharing disabled (can be configured)
- **CSRF**: Cross-Site Request Forgery protection disabled for REST API

## Implementation Details

### Security Configuration
The security is configured in `SecurityConfig.java` with:
- JWT authentication filter
- Role-based access control
- HTTP method-specific permissions
- Path-based security rules

### Authentication Flow
1. User submits credentials via `/api/auth/login`
2. System validates credentials and generates JWT token
3. Client includes JWT token in Authorization header
4. JWT filter validates token on each request
5. User context is established for the request

### Authorization Flow
1. Extract user roles from JWT token
2. Check if user has required role for the endpoint
3. Allow or deny access based on role permissions

## Best Practices

### For Frontend Development
- Store JWT tokens securely (consider httpOnly cookies)
- Implement proper token refresh logic
- Handle authentication errors gracefully
- Clear tokens on logout

### For Backend Development
- Use method-level security annotations when needed
- Implement proper exception handling
- Log security events for monitoring
- Regular security audits

### For Production Deployment
- Use HTTPS for all communications
- Implement rate limiting
- Monitor authentication attempts
- Regular security updates

## Configuration Examples

### Adding New Secured Endpoint
```java
.requestMatchers(HttpMethod.POST, "/api/new-feature").hasRole("ADMIN")
```

### Method-Level Security
```java
@PreAuthorize("hasRole('ADMIN') or @userService.isOwner(authentication.name, #userId)")
public void updateUser(Long userId, UserDto userDto) {
    // Implementation
}
```

### Custom Security Expression
```java
@PreAuthorize("@securityService.canAccessBook(authentication.name, #bookId)")
public BookDto getBookDetails(Long bookId) {
    // Implementation
}
```

## Security Considerations

### Current Implementation
✅ JWT-based authentication  
✅ Role-based authorization  
✅ Password encryption  
✅ Token blacklisting  
✅ Method-level security support  

### Recommended Enhancements
🔄 Add request rate limiting  
🔄 Implement account lockout after failed attempts  
🔄 Add two-factor authentication  
🔄 Implement session timeout  
🔄 Add security headers  
🔄 Enable CORS with proper configuration  
🔄 Add audit logging  

## Troubleshooting

### Common Issues
1. **403 Forbidden**: Check user roles and endpoint permissions
2. **401 Unauthorized**: Verify JWT token is valid and not expired
3. **Token Refresh Failed**: Check refresh token validity
4. **Role Not Found**: Ensure user has proper role assigned

### Testing Security
- Use tools like Postman or curl to test endpoints
- Verify different user roles have appropriate access
- Test token expiration and refresh flows
- Validate that unauthorized access is properly blocked 