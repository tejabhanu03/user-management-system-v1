# User Management System API Documentation

## Base URL
```
http://localhost:8080/api
```

## Endpoints

### Health Check
- **GET** `/health` - Check system health status

### Home
- **GET** `/` - Welcome message and version info

## Dashboard Statistics

### Overview Statistics
- **GET** `/statistics/overview`
- Description: Returns summary statistics for homepage dashboards.
- Response:
```json
{
  "onboardedClientsCount": 12,
  "verifiedUsersCount": 248
}
```

## Authentication Endpoints

### Login
- **POST** `/auth/login`
- Request Body:
```json
{
  "username": "string",
  "password": "string"
}
```
- Response: LoginResponse with session token, JWT, roles, and permissions

Example response:

```json
{
  "userId": "uuid",
  "username": "john_doe",
  "email": "john@example.com",
  "sessionToken": "session-token",
  "jwtToken": "jwt-token",
  "roles": ["ADMIN"],
  "permissions": ["user.read", "user.write"],
  "message": "Login successful"
}
```

### Logout
- **POST** `/auth/logout`
- Headers: `Authorization: Bearer <sessionToken>`
- Response: Success message

### Validate Session
- **GET** `/auth/validate`
- Headers: `Authorization: Bearer <sessionToken>`
- Response: `{ "valid": boolean }`

> Note: For microservice-to-microservice communication, the recommended approach is to rely on the `jwtToken` instead of session validation.

## User Context Endpoint (for Microservices)

- **GET** `/user-context/me`
- Headers: `Authorization: Bearer <jwtToken>`
- Description: Returns the current authenticated user's context (identity, roles, and permissions) based on the JWT.

Example response:

```json
{
  "userId": "uuid",
  "username": "john_doe",
  "roles": ["ADMIN"],
  "permissions": ["user.read", "user.write"]
}
```

## User Endpoints

### Register User
- **POST** `/users/register`
- Request Body:
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "fullName": "string"
}
```

### Get User by ID
- **GET** `/users/{userId}`

### Get User by Username
- **GET** `/users/username/{username}`

### Get All Users
- **GET** `/users`

### Update User
- **PUT** `/users/{userId}`
- Request Body: Same as registration

### Deactivate User
- **DELETE** `/users/{userId}`

### Activate User
- **POST** `/users/{userId}/activate`

## Role Endpoints

### Create Role
- **POST** `/roles`
- Request Body:
```json
{
  "roleName": "string",
  "description": "string"
}
```

### Get Role by ID
- **GET** `/roles/{roleId}`

### Get Role by Name
- **GET** `/roles/name/{roleName}`

### Get All Roles
- **GET** `/roles`

### Assign Role to User
- **POST** `/roles/assign`
- Request Body:
```json
{
  "userId": number,
  "roleId": number
}
```

### Remove Role from User
- **POST** `/roles/remove`
- Request Body:
```json
{
  "userId": number,
  "roleId": number
}
```

### Get User Roles
- **GET** `/roles/user/{userId}`

## Permission Endpoints

### Create Permission
- **POST** `/permissions`
- Request Body:
```json
{
  "permissionName": "string",
  "description": "string"
}
```

### Get Permission by ID
- **GET** `/permissions/{permissionId}`

### Get Permission by Name
- **GET** `/permissions/name/{permissionName}`

### Get All Permissions
- **GET** `/permissions`

### Assign Permission to Role
- **POST** `/permissions/assign`
- Request Body:
```json
{
  "roleId": number,
  "permissionId": number
}
```

### Remove Permission from Role
- **POST** `/permissions/remove`
- Request Body:
```json
{
  "roleId": number,
  "permissionId": number
}
```

### Get Role Permissions
- **GET** `/permissions/role/{roleId}`

## Session Endpoints

### Get User Active Sessions
- **GET** `/sessions/user/{userId}/active`

### Get All User Sessions
- **GET** `/sessions/user/{userId}`

### Get Session by Token
- **GET** `/sessions/{sessionToken}`

### Invalidate All User Sessions
- **POST** `/sessions/user/{userId}/invalidate-all`

## Response Format

Success Response:
```json
{
  "id": number,
  "data": {}
}
```

Error Response:
```json
{
  "timestamp": "2026-03-03T21:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error description",
  "path": "/api/endpoint"
}
```

## Status Codes
- 200 OK
- 201 Created
- 204 No Content
- 400 Bad Request
- 401 Unauthorized
- 404 Not Found
- 500 Internal Server Error
