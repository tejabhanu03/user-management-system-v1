#!/bin/bash

# User Management System - API Testing Script
# This file contains curl commands to test all endpoints

BASE_URL="http://localhost:8080/api"

echo "=== User Management System API Testing ==="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 1. Health Check
echo -e "${BLUE}1. Health Check${NC}"
curl -X GET $BASE_URL/health
echo ""
echo ""

# 2. Home
echo -e "${BLUE}2. Home${NC}"
curl -X GET $BASE_URL/
echo ""
echo ""

# 3. Register Users
echo -e "${BLUE}3. Register User 1${NC}"
USER1=$(curl -s -X POST $BASE_URL/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com",
    "fullName": "John Doe"
  }')
echo $USER1
echo ""
echo ""

echo -e "${BLUE}4. Register User 2${NC}"
USER2=$(curl -s -X POST $BASE_URL/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jane_smith",
    "password": "securepass456",
    "email": "jane@example.com",
    "fullName": "Jane Smith"
  }')
echo $USER2
echo ""
echo ""

# 5. Get All Users
echo -e "${BLUE}5. Get All Users${NC}"
curl -X GET $BASE_URL/users
echo ""
echo ""

# 6. Get User by ID
echo -e "${BLUE}6. Get User by ID (1)${NC}"
curl -X GET $BASE_URL/users/1
echo ""
echo ""

# 7. Get User by Username
echo -e "${BLUE}7. Get User by Username${NC}"
curl -X GET $BASE_URL/users/username/john_doe
echo ""
echo ""

# 8. Create Roles
echo -e "${BLUE}8. Create Role - ADMIN${NC}"
ROLE_ADMIN=$(curl -s -X POST $BASE_URL/roles \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "ADMIN",
    "description": "Administrator role"
  }')
echo $ROLE_ADMIN
echo ""
echo ""

echo -e "${BLUE}9. Create Role - USER${NC}"
ROLE_USER=$(curl -s -X POST $BASE_URL/roles \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "USER",
    "description": "Regular user role"
  }')
echo $ROLE_USER
echo ""
echo ""

# 10. Get All Roles
echo -e "${BLUE}10. Get All Roles${NC}"
curl -X GET $BASE_URL/roles
echo ""
echo ""

# 11. Create Permissions
echo -e "${BLUE}11. Create Permission - READ${NC}"
PERM_READ=$(curl -s -X POST $BASE_URL/permissions \
  -H "Content-Type: application/json" \
  -d '{
    "permissionName": "READ",
    "description": "Read permission"
  }')
echo $PERM_READ
echo ""
echo ""

echo -e "${BLUE}12. Create Permission - WRITE${NC}"
PERM_WRITE=$(curl -s -X POST $BASE_URL/permissions \
  -H "Content-Type: application/json" \
  -d '{
    "permissionName": "WRITE",
    "description": "Write permission"
  }')
echo $PERM_WRITE
echo ""
echo ""

echo -e "${BLUE}13. Create Permission - DELETE${NC}"
PERM_DELETE=$(curl -s -X POST $BASE_URL/permissions \
  -H "Content-Type: application/json" \
  -d '{
    "permissionName": "DELETE",
    "description": "Delete permission"
  }')
echo $PERM_DELETE
echo ""
echo ""

# 14. Assign Role to User
echo -e "${BLUE}14. Assign ADMIN role to User 1${NC}"
curl -X POST $BASE_URL/roles/assign \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "roleId": 1
  }'
echo ""
echo ""

echo -e "${BLUE}15. Assign USER role to User 2${NC}"
curl -X POST $BASE_URL/roles/assign \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "roleId": 2
  }'
echo ""
echo ""

# 16. Get User Roles
echo -e "${BLUE}16. Get User 1 Roles${NC}"
curl -X GET $BASE_URL/roles/user/1
echo ""
echo ""

# 17. Assign Permissions to Roles
echo -e "${BLUE}17. Assign READ permission to ADMIN role${NC}"
curl -X POST $BASE_URL/permissions/assign \
  -H "Content-Type: application/json" \
  -d '{
    "roleId": 1,
    "permissionId": 1
  }'
echo ""
echo ""

echo -e "${BLUE}18. Assign WRITE permission to ADMIN role${NC}"
curl -X POST $BASE_URL/permissions/assign \
  -H "Content-Type: application/json" \
  -d '{
    "roleId": 1,
    "permissionId": 2
  }'
echo ""
echo ""

echo -e "${BLUE}19. Assign DELETE permission to ADMIN role${NC}"
curl -X POST $BASE_URL/permissions/assign \
  -H "Content-Type: application/json" \
  -d '{
    "roleId": 1,
    "permissionId": 3
  }'
echo ""
echo ""

echo -e "${BLUE}20. Assign READ permission to USER role${NC}"
curl -X POST $BASE_URL/permissions/assign \
  -H "Content-Type: application/json" \
  -d '{
    "roleId": 2,
    "permissionId": 1
  }'
echo ""
echo ""

# 21. Get Role Permissions
echo -e "${BLUE}21. Get ADMIN role permissions${NC}"
curl -X GET $BASE_URL/permissions/role/1
echo ""
echo ""

# 22. Login User
echo -e "${BLUE}22. Login User 1${NC}"
LOGIN_RESPONSE=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }')
echo $LOGIN_RESPONSE
SESSION_TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"sessionToken":"[^"]*' | cut -d'"' -f4)
echo "Session Token: $SESSION_TOKEN"
echo ""
echo ""

# 23. Validate Session
echo -e "${BLUE}23. Validate Session${NC}"
curl -X GET $BASE_URL/auth/validate \
  -H "Authorization: Bearer $SESSION_TOKEN"
echo ""
echo ""

# 24. Get Active Sessions
echo -e "${BLUE}24. Get User 1 Active Sessions${NC}"
curl -X GET $BASE_URL/sessions/user/1/active
echo ""
echo ""

# 25. Get All Sessions
echo -e "${BLUE}25. Get User 1 All Sessions${NC}"
curl -X GET $BASE_URL/sessions/user/1
echo ""
echo ""

# 26. Get All Permissions
echo -e "${BLUE}26. Get All Permissions${NC}"
curl -X GET $BASE_URL/permissions
echo ""
echo ""

# 27. Update User
echo -e "${BLUE}27. Update User 1 Full Name${NC}"
curl -X PUT $BASE_URL/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com",
    "fullName": "John Doe Updated"
  }'
echo ""
echo ""

# 28. Logout User
echo -e "${BLUE}28. Logout User${NC}"
curl -X POST $BASE_URL/auth/logout \
  -H "Authorization: Bearer $SESSION_TOKEN"
echo ""
echo ""

# 29. Validate Session After Logout
echo -e "${BLUE}29. Validate Session After Logout${NC}"
curl -X GET $BASE_URL/auth/validate \
  -H "Authorization: Bearer $SESSION_TOKEN"
echo ""
echo ""

# 30. Deactivate User
echo -e "${BLUE}30. Deactivate User 2${NC}"
curl -X DELETE $BASE_URL/users/2
echo ""
echo ""

echo -e "${GREEN}=== Testing Complete ===${NC}"

