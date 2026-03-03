# User Management System v1

A Spring Boot application for managing users, roles, permissions, and sessions with login/logout functionality.

## Features

- **User Management**: Register, retrieve, update, and manage user accounts
- **Authentication**: Login and logout with secure session tokens
- **Role Management**: Create and assign roles to users
- **Permission Management**: Define permissions and assign them to roles
- **Session Management**: Track active sessions with IP and user agent information
- **MySQL Database**: Persistent data storage with Hibernate ORM
- **Security**: Password encryption using BCrypt

## Technology Stack

- Java 21
- Spring Boot 4.0.3
- Spring Security
- Spring Data JPA
- Hibernate ORM
- MySQL 8+
- Lombok
- Maven

## Prerequisites

- Java 21 or higher
- MySQL 8+ running on localhost:3306
- Maven 3.6+

## Setup Instructions

### 1. Database Setup

Create a MySQL database with the following credentials:

```sql
CREATE DATABASE user_management;
-- Use root user with password 'pass'
```

### 2. Clone and Build

```bash
cd /path/to/user-management
mvn clean install
```

### 3. Configure Database

Update `src/main/resources/application.yaml` with your MySQL credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/user_management
    username: root
    password: pass
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

See [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for complete API endpoints and usage examples.

### Quick Start

#### 1. Check Health
```bash
curl http://localhost:8080/api/health
```

#### 2. Register a User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com",
    "fullName": "John Doe"
  }'
```

#### 3. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

#### 4. Create a Role
```bash
curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "ADMIN",
    "description": "Administrator role"
  }'
```

#### 5. Assign Role to User
```bash
curl -X POST http://localhost:8080/api/roles/assign \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "roleId": 1
  }'
```

## Project Structure

```
src/main/java/com/use_management_system/user_management/
├── config/              # Spring configuration
│   └── SecurityConfig.java
├── controller/          # REST controllers
│   ├── AuthenticationController.java
│   ├── HomeController.java
│   ├── PermissionController.java
│   ├── RoleController.java
│   ├── SessionController.java
│   └── UserController.java
├── dto/                 # Data Transfer Objects
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── UserRegistrationRequest.java
│   └── UserResponse.java
├── entity/              # JPA Entities
│   ├── Permission.java
│   ├── Role.java
│   ├── RolePermission.java
│   ├── Session.java
│   ├── User.java
│   └── UserRole.java
├── exception/           # Exception handling
│   └── GlobalExceptionHandler.java
├── repository/          # Data access layer
│   ├── PermissionRepository.java
│   ├── RolePermissionRepository.java
│   ├── RoleRepository.java
│   ├── SessionRepository.java
│   ├── UserRepository.java
│   └── UserRoleRepository.java
├── service/             # Business logic
│   ├── AuthenticationService.java
│   ├── PermissionService.java
│   ├── RoleService.java
│   ├── SessionService.java
│   └── UserService.java
├── util/                # Utility classes
│   └── TokenUtil.java
└── UserManagementApplication.java
```

## Database Schema

### Tables

- `users` - User accounts
- `roles` - Role definitions
- `permissions` - Permission definitions
- `user_roles` - User to Role mapping
- `role_permissions` - Role to Permission mapping
- `sessions` - Active and inactive user sessions

## Running Tests

```bash
mvn test
```

Tests use H2 in-memory database configured in `src/test/resources/application.yaml`

## Logging

Logging is configured in `application.yaml`:
- Root level: INFO
- Application level: DEBUG

## Error Handling

Global exception handler provides consistent error responses with:
- Timestamp
- HTTP Status Code
- Error Type
- Error Message
- Request Path

## Security Considerations

- Passwords are encrypted using BCrypt
- Session tokens are generated using SecureRandom
- Sessions are tracked with IP address and User Agent
- Active session validation on each request

## Future Enhancements

- JWT token support
- OAuth2 integration
- Rate limiting
- Audit logging
- Email verification
- Two-factor authentication

## License

This project is part of the User Management System learning series.

## Author

Bhanu Teja

## Support

For issues or questions, please refer to the API documentation or create an issue in the repository.
