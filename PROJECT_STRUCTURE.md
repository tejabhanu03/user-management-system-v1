# User Management System - Project Structure

```
user-management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/use_management_system/user_management/
│   │   │       ├── config/
│   │   │       │   └── SecurityConfig.java
│   │   │       │       └── BCrypt password encoder configuration
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── AuthenticationController.java
│   │   │       │   │   ├── POST /auth/login
│   │   │       │   │   ├── POST /auth/logout
│   │   │       │   │   └── GET /auth/validate
│   │   │       │   │
│   │   │       │   ├── HomeController.java
│   │   │       │   │   ├── GET /health
│   │   │       │   │   └── GET /
│   │   │       │   │
│   │   │       │   ├── PermissionController.java
│   │   │       │   │   ├── POST /permissions
│   │   │       │   │   ├── GET /permissions
│   │   │       │   │   ├── GET /permissions/{id}
│   │   │       │   │   ├── GET /permissions/name/{name}
│   │   │       │   │   ├── POST /permissions/assign
│   │   │       │   │   ├── POST /permissions/remove
│   │   │       │   │   └── GET /permissions/role/{roleId}
│   │   │       │   │
│   │   │       │   ├── RoleController.java
│   │   │       │   │   ├── POST /roles
│   │   │       │   │   ├── GET /roles
│   │   │       │   │   ├── GET /roles/{id}
│   │   │       │   │   ├── GET /roles/name/{name}
│   │   │       │   │   ├── POST /roles/assign
│   │   │       │   │   ├── POST /roles/remove
│   │   │       │   │   └── GET /roles/user/{userId}
│   │   │       │   │
│   │   │       │   ├── SessionController.java
│   │   │       │   │   ├── GET /sessions/user/{userId}/active
│   │   │       │   │   ├── GET /sessions/user/{userId}
│   │   │       │   │   ├── GET /sessions/{token}
│   │   │       │   │   └── POST /sessions/user/{userId}/invalidate-all
│   │   │       │   │
│   │   │       │   └── UserController.java
│   │   │       │       ├── POST /users/register
│   │   │       │       ├── GET /users
│   │   │       │       ├── GET /users/{id}
│   │   │       │       ├── GET /users/username/{username}
│   │   │       │       ├── PUT /users/{id}
│   │   │       │       ├── DELETE /users/{id}
│   │   │       │       └── POST /users/{id}/activate
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   ├── LoginRequest.java
│   │   │       │   ├── LoginResponse.java
│   │   │       │   ├── UserRegistrationRequest.java
│   │   │       │   └── UserResponse.java
│   │   │       │
│   │   │       ├── entity/
│   │   │       │   ├── Permission.java
│   │   │       │   │   └── Permissions for role-based access control
│   │   │       │   │
│   │   │       │   ├── Role.java
│   │   │       │   │   └── User roles for authorization
│   │   │       │   │
│   │   │       │   ├── RolePermission.java
│   │   │       │   │   └── Many-to-many: Role <-> Permission
│   │   │       │   │
│   │   │       │   ├── Session.java
│   │   │       │   │   └── User session tracking with metadata
│   │   │       │   │
│   │   │       │   ├── User.java
│   │   │       │   │   └── User account information
│   │   │       │   │
│   │   │       │   └── UserRole.java
│   │   │       │       └── Many-to-many: User <-> Role
│   │   │       │
│   │   │       ├── exception/
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       │       └── Centralized error handling
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   ├── PermissionRepository.java
│   │   │       │   ├── RolePermissionRepository.java
│   │   │       │   ├── RoleRepository.java
│   │   │       │   ├── SessionRepository.java
│   │   │       │   ├── UserRepository.java
│   │   │       │   └── UserRoleRepository.java
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── AuthenticationService.java
│   │   │       │   │   └── Login, logout, session validation
│   │   │       │   │
│   │   │       │   ├── PermissionService.java
│   │   │       │   │   └── Permission management and assignment
│   │   │       │   │
│   │   │       │   ├── RoleService.java
│   │   │       │   │   └── Role management and assignment
│   │   │       │   │
│   │   │       │   ├── SessionService.java
│   │   │       │   │   └── Session tracking and management
│   │   │       │   │
│   │   │       │   └── UserService.java
│   │   │       │       └── User registration and management
│   │   │       │
│   │   │       ├── util/
│   │   │       │   └── TokenUtil.java
│   │   │       │       └── Secure token generation
│   │   │       │
│   │   │       └── UserManagementApplication.java
│   │   │           └── Spring Boot main application
│   │   │
│   │   └── resources/
│   │       └── application.yaml
│   │           └── MySQL configuration for production
│   │
│   └── test/
│       └── resources/
│           └── application.yaml
│               └── H2 in-memory database for testing
│
├── mvnw
├── mvnw.cmd
├── pom.xml
│   └── Project dependencies and build configuration
│
├── README.md
│   └── Project overview and setup instructions
│
├── API_DOCUMENTATION.md
│   └── Complete API endpoint reference
│
├── IMPLEMENTATION_SUMMARY.md
│   └── Implementation details and statistics
│
├── TEST_API.sh
│   └── Bash script with curl commands for API testing
│
└── PROJECT_STRUCTURE.md (this file)
    └── Visual guide of project organization
```

## Technology Stack

### Core Framework
- **Spring Boot 4.0.3** - Application framework
- **Java 21** - Programming language

### Database & ORM
- **MySQL 8+** - Production database
- **H2** - In-memory testing database
- **Hibernate** - ORM framework
- **Spring Data JPA** - Data access layer

### Security
- **Spring Security** - Security framework
- **BCrypt** - Password encryption
- **SecureRandom** - Token generation

### Additional Libraries
- **Lombok** - Boilerplate code reduction
- **Spring Validation** - Input validation

### Build Tool
- **Maven 3.6+** - Dependency management and build automation

## Package Organization

### config/
Configures Spring beans and framework settings
- Password encoder setup

### controller/
REST API endpoints and HTTP request handling
- 6 controllers
- 31+ endpoints

### service/
Business logic and core functionality
- 5 service classes
- Database operation orchestration

### repository/
Data access and database queries
- 6 repository interfaces
- Spring Data JPA queries

### entity/
JPA entities and database models
- 6 entity classes
- Table mappings

### dto/
Data transfer between layers
- Request DTOs
- Response DTOs

### util/
Utility functions and helper classes
- Token generation

### exception/
Error handling and exception management
- Global exception handler

## Database Tables

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255),
    active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE user_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    active BOOLEAN,
    assigned_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE role_permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    active BOOLEAN,
    assigned_at TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

CREATE TABLE sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(255) UNIQUE NOT NULL,
    ip_address VARCHAR(255),
    user_agent TEXT,
    login_at TIMESTAMP,
    last_activity_at TIMESTAMP,
    logout_at TIMESTAMP,
    active BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## API Response Formats

### Success Response
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "active": true,
  "createdAt": "2026-03-03T21:00:00",
  "updatedAt": "2026-03-03T21:00:00"
}
```

### Error Response
```json
{
  "timestamp": "2026-03-03T21:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Username already exists",
  "path": "/api/users/register"
}
```

## Security Flow

```
User Login
    ↓
Username/Password Validation
    ↓
BCrypt Password Comparison
    ↓
Session Token Generation
    ↓
Session Record Creation
    ↓
Return Token to Client
    ↓
Client Includes Token in Requests
    ↓
Session Validation on Each Request
    ↓
Activity Timestamp Update
    ↓
User Logout (Optional)
    ↓
Session Deactivation
```

## Authentication Flow

1. User registers with username, password, email
2. Password is encrypted using BCrypt
3. User logs in with credentials
4. System validates credentials against encrypted password
5. Secure session token is generated
6. Session record is created with IP and User Agent
7. Token is returned to client
8. Client includes token in subsequent requests
9. Token validation ensures session is active
10. Activity timestamp is updated
11. User can logout to invalidate session

## Role-Based Access Control (RBAC)

```
User
 ├─ Role 1 (ADMIN)
 │  ├─ Permission 1 (READ)
 │  ├─ Permission 2 (WRITE)
 │  └─ Permission 3 (DELETE)
 └─ Role 2 (USER)
    └─ Permission 1 (READ)
```

## File Size Estimates

- **Controllers**: ~15 KB
- **Services**: ~20 KB
- **Entities**: ~12 KB
- **Repositories**: ~8 KB
- **DTOs**: ~5 KB
- **Configuration**: ~2 KB
- **Utilities**: ~1 KB
- **Total Java Code**: ~60+ KB

## Deployment Checklist

- [ ] Database created and accessible
- [ ] MySQL running on localhost:3306
- [ ] credentials configured (root/pass)
- [ ] Maven build succeeds
- [ ] No compilation errors
- [ ] All dependencies resolved
- [ ] Application starts without errors
- [ ] API endpoints respond
- [ ] Database tables created

---

**Last Updated**: March 3, 2026
**Version**: 1.0.0
**Status**: ✅ Complete and Ready for Deployment

