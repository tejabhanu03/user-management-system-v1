# 🚀 User Management System - Quick Reference Guide

## ⚡ Quick Start (5 minutes)

```bash
# 1. Create database
mysql -u root -p pass
> CREATE DATABASE user_management;
> EXIT;

# 2. Build project
cd /path/to/user-management
mvn clean install

# 3. Run application
mvn spring-boot:run

# 4. Test API (in another terminal)
curl http://localhost:8080/api/health
```

---

## 🔗 Main Endpoints

### Authentication
```bash
# Login
POST /api/auth/login
Body: {"username": "user", "password": "pass"}

# Logout
POST /api/auth/logout
Header: Authorization: Bearer <token>

# Validate
GET /api/auth/validate
Header: Authorization: Bearer <token>
```

### Users
```bash
# Register
POST /api/users/register
Body: {"username": "john", "password": "pass", "email": "john@example.com", "fullName": "John Doe"}

# Get all
GET /api/users

# Get by ID
GET /api/users/{id}

# Update
PUT /api/users/{id}

# Deactivate
DELETE /api/users/{id}
```

### Roles
```bash
# Create
POST /api/roles
Body: {"roleName": "ADMIN", "description": "Admin role"}

# Get all
GET /api/roles

# Assign to user
POST /api/roles/assign
Body: {"userId": 1, "roleId": 1}

# Get user roles
GET /api/roles/user/{userId}
```

### Permissions
```bash
# Create
POST /api/permissions
Body: {"permissionName": "READ", "description": "Read access"}

# Get all
GET /api/permissions

# Assign to role
POST /api/permissions/assign
Body: {"roleId": 1, "permissionId": 1}

# Get role permissions
GET /api/permissions/role/{roleId}
```

### Sessions
```bash
# Get active sessions
GET /api/sessions/user/{userId}/active

# Get all sessions
GET /api/sessions/user/{userId}

# Invalidate all
POST /api/sessions/user/{userId}/invalidate-all
```

---

## 📁 Project Structure

```
user-management/
├── src/main/java/com/use_management_system/user_management/
│   ├── config/          (Security config)
│   ├── controller/      (6 REST controllers)
│   ├── dto/            (DTOs for API)
│   ├── entity/         (6 database entities)
│   ├── exception/      (Error handling)
│   ├── repository/     (6 data access)
│   ├── service/        (5 business logic)
│   └── util/           (Token generation)
├── src/main/resources/application.yaml
├── src/test/resources/application.yaml
├── pom.xml
├── README.md
├── API_DOCUMENTATION.md
├── TEST_API.sh
└── [7 more documentation files]
```

---

## 🔧 Configuration

### Production (src/main/resources/application.yaml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/user_management
    username: root
    password: pass
  jpa:
    hibernate:
      ddl-auto: update
```

### Test (src/test/resources/application.yaml)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
```

---

## 📊 Database Schema (6 Tables)

| Table | Purpose |
|-------|---------|
| users | User accounts |
| roles | Role definitions |
| permissions | Permission definitions |
| user_roles | User-to-Role mapping |
| role_permissions | Role-to-Permission mapping |
| sessions | Session tracking |

---

## 🔐 Security

- **Passwords**: BCrypt encryption
- **Tokens**: SecureRandom generation
- **Sessions**: IP + User Agent tracking
- **Validation**: Input sanitization

---

## 📡 API Response

### Success (200)
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "active": true
}
```

### Error (400)
```json
{
  "timestamp": "2026-03-03T21:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error description"
}
```

---

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| README.md | Setup & overview |
| API_DOCUMENTATION.md | All endpoints |
| PROJECT_STRUCTURE.md | Code organization |
| IMPLEMENTATION_SUMMARY.md | Architecture |
| CHECKLIST.md | Verification |
| DOCUMENTATION_INDEX.md | Navigation |
| FILES_CREATED.md | File listing |
| IMPLEMENTATION_COMPLETE.md | Summary |

---

## 🧪 Testing

### Automated Tests
```bash
bash TEST_API.sh
```

### Manual Test Example
```bash
# 1. Register
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123","email":"test@example.com","fullName":"Test User"}'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'

# 3. Get User (with token)
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer <token>"
```

---

## 🎯 API Statistics

| Metric | Count |
|--------|-------|
| Controllers | 6 |
| Endpoints | 31 |
| Services | 5 |
| Repositories | 6 |
| Entities | 6 |
| Database Tables | 6 |

---

## ⚙️ Environment

- **Language**: Java 21
- **Framework**: Spring Boot 4.0.3
- **Database**: MySQL 8+
- **Server**: Tomcat (port 8080)
- **Build**: Maven

---

## 🚨 Troubleshooting

### Database Connection Failed
```
✗ Check MySQL is running
✗ Verify database 'user_management' exists
✗ Check credentials (root/pass)
✗ Verify host (localhost:3306)
```

### Build Failed
```
✗ Check Java version (21+)
✗ Clear Maven cache: mvn clean
✗ Update dependencies: mvn install
```

### Port Already in Use
```
✗ Change port in application.yaml (server.port)
✗ Or stop other app using 8080
```

---

## 📋 Common Tasks

### Create Admin User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "email": "admin@example.com",
    "fullName": "Administrator"
  }'
```

### Assign Admin Role
```bash
# 1. Create ADMIN role
curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -d '{"roleName": "ADMIN", "description": "Admin role"}'

# 2. Assign to user
curl -X POST http://localhost:8080/api/roles/assign \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "roleId": 1}'
```

### Add Permissions
```bash
curl -X POST http://localhost:8080/api/permissions \
  -H "Content-Type: application/json" \
  -d '{"permissionName": "READ", "description": "Read permission"}'
```

---

## 🔍 Debugging

### Enable Debug Logging
In application.yaml:
```yaml
logging:
  level:
    com.use_management_system: DEBUG
```

### View Active Sessions
```bash
curl http://localhost:8080/api/sessions/user/1/active
```

### Check System Health
```bash
curl http://localhost:8080/api/health
```

---

## 📞 Support References

| Question | Reference |
|----------|-----------|
| How to setup? | README.md |
| What endpoints? | API_DOCUMENTATION.md |
| How does it work? | IMPLEMENTATION_SUMMARY.md |
| Where are files? | PROJECT_STRUCTURE.md |
| Is complete? | CHECKLIST.md |
| How to test? | TEST_API.sh |

---

## ✅ Pre-deployment Checklist

- [ ] MySQL database created
- [ ] application.yaml configured
- [ ] `mvn clean install` succeeds
- [ ] No compilation errors
- [ ] Application starts
- [ ] API endpoints respond
- [ ] TEST_API.sh passes

---

## 🎉 You're Ready!

The User Management System is ready to use. Start with the API documentation or run the test script to verify everything works.

---

**Version**: 1.0.0
**Status**: ✅ Production Ready
**Date**: March 3, 2026

---

*For detailed information, see DOCUMENTATION_INDEX.md*

