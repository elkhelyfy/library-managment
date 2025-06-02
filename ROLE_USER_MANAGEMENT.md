# Role and User Management System

## Overview
This document describes the comprehensive role and user management system implemented in the Biblio application.

## Architecture

### Role Management
- **Role Entity**: Stores role information with name, display name, and description
- **Role Service**: Business logic for role operations
- **Role Controller**: REST API endpoints for role management
- **Role Repository**: Data access layer for roles

### User Management
- **Enhanced User System**: Integration with role-based permissions
- **User Management Controller**: Administrative operations for user management
- **User Statistics**: Analytics and reporting for user data

## Default Roles

The system automatically creates three default roles on startup:

1. **ROLE_ADMIN** (Administrator)
   - Full system administrator with all permissions
   - Can manage users, roles, and system settings

2. **ROLE_USER** (User)
   - Standard user with basic permissions
   - Default role for new registrations

3. **ROLE_MODERATOR** (Moderator)
   - Moderator with limited administrative permissions
   - Intermediate level between user and admin

## API Endpoints

### Role Management (`/api/roles`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/roles` | Get all roles |
| GET | `/api/roles/{id}` | Get role by ID |
| GET | `/api/roles/name/{name}` | Get role by name |
| POST | `/api/roles` | Create new role |
| PUT | `/api/roles/{id}` | Update role |
| DELETE | `/api/roles/{id}` | Delete role |
| GET | `/api/roles/exists/{name}` | Check if role exists |
| POST | `/api/roles/initialize` | Initialize default roles |

### User Management (`/api/admin/users`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/users` | Get all users for management |
| GET | `/api/admin/users/{id}` | Get user management details |
| PUT | `/api/admin/users/{id}/role` | Update user role |
| PUT | `/api/admin/users/{id}/status` | Update user status |
| POST | `/api/admin/users/{id}/activate` | Activate user |
| POST | `/api/admin/users/{id}/deactivate` | Deactivate user |
| POST | `/api/admin/users/{id}/suspend` | Suspend user |
| POST | `/api/admin/users/{id}/make-admin` | Make user admin |
| POST | `/api/admin/users/{id}/make-user` | Make user regular |
| POST | `/api/admin/users/{id}/make-moderator` | Make user moderator |
| GET | `/api/admin/users/stats` | Get user statistics |
| GET | `/api/admin/users/search` | Search users with filters |

## Data Models

### Role Entity
```json
{
  "id": 1,
  "name": "ROLE_ADMIN",
  "displayName": "Administrator",
  "description": "Full system administrator with all permissions",
  "createdAt": "2023-12-01T10:00:00",
  "updatedAt": "2023-12-01T10:00:00"
}
```

### User Management DTO
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "ROLE_USER",
  "roleDisplayName": "User",
  "firstName": "John",
  "lastName": "Doe",
  "fullName": "John Doe",
  "status": "ACTIVE",
  "createdAt": "2023-12-01T10:00:00",
  "updatedAt": "2023-12-01T10:00:00",
  "isActive": true,
  "isAdmin": false,
  "availableRoles": ["ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"],
  "availableStatuses": ["ACTIVE", "INACTIVE", "SUSPENDED"]
}
```

### User Statistics
```json
{
  "totalUsers": 100,
  "activeUsers": 85,
  "inactiveUsers": 10,
  "suspendedUsers": 5,
  "adminUsers": 3,
  "regularUsers": 95,
  "moderatorUsers": 2
}
```

## Usage Examples

### Creating a New Role
```bash
curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "ROLE_MANAGER",
    "displayName": "Manager",
    "description": "Department manager with specific permissions"
  }'
```

### Making a User Admin
```bash
curl -X POST http://localhost:8080/api/admin/users/1/make-admin \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Getting User Statistics
```bash
curl -X GET http://localhost:8080/api/admin/users/stats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Searching Users
```bash
curl -X GET "http://localhost:8080/api/admin/users/search?role=ROLE_ADMIN&status=ACTIVE" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## User Status Management

The system supports three user statuses:

1. **ACTIVE**: User can log in and use the system
2. **INACTIVE**: User cannot log in but account exists
3. **SUSPENDED**: User is temporarily blocked from the system

## Role Validation

- Role names must be unique
- Role names are automatically prefixed with "ROLE_" if not present
- Default roles cannot be deleted (optional protection can be added)

## Security Features

- JWT-based authentication
- Role-based access control
- User status validation during authentication
- Audit trail with creation and update timestamps

## Database Schema

### Roles Table
```sql
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100),
    description VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Users Table (Enhanced)
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

## Integration Notes

- The role management system integrates seamlessly with existing JWT authentication
- User roles are included in JWT tokens for authorization
- The system maintains backward compatibility with the existing string-based role system
- Default roles are automatically created on application startup

## Future Enhancements

Potential improvements for the role and user management system:

1. **Permission-based Authorization**: Assign specific permissions to roles
2. **Role Hierarchy**: Define parent-child relationships between roles
3. **Bulk Operations**: Mass user management operations
4. **Audit Logging**: Detailed logs of user and role changes
5. **Email Notifications**: Notify users of role/status changes
6. **Advanced Search**: More sophisticated user search and filtering
7. **Export/Import**: Data export and import capabilities
8. **Role Templates**: Predefined role configurations 