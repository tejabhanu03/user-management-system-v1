# Flyway Database Migrations

## Overview

This project uses **Flyway** for database schema versioning and migrations. All SQL migration scripts are located in `src/main/resources/db/migration/`.

## Migration Files

### Naming Convention
Migrations follow Flyway's naming convention: `V{version}__{description}.sql`

- `V` - Version indicator
- `{version}` - Numeric version (1, 2, 3, etc.)
- `__` - Double underscore separator
- `{description}` - Migration description with underscores instead of spaces

### Migration Scripts

#### V1__Create_users_table.sql
Creates the `users` table for storing user account information.

**Columns:**
- `id` - Primary key
- `username` - Unique username
- `password` - Encrypted password
- `email` - Unique email address
- `full_name` - User's full name
- `active` - Boolean flag for user status
- `created_at` - Timestamp when user was created
- `updated_at` - Timestamp when user was last updated

**Indexes:**
- Username, Email, Active, Created At

---

#### V2__Create_roles_table.sql
Creates the `roles` table for defining user roles.

**Columns:**
- `id` - Primary key
- `role_name` - Unique role name
- `description` - Role description
- `active` - Boolean flag for role status
- `created_at` - Creation timestamp
- `updated_at` - Update timestamp

**Indexes:**
- Role Name, Active

---

#### V3__Create_permissions_table.sql
Creates the `permissions` table for defining system permissions.

**Columns:**
- `id` - Primary key
- `permission_name` - Unique permission name
- `description` - Permission description
- `active` - Boolean flag for permission status
- `created_at` - Creation timestamp
- `updated_at` - Update timestamp

**Indexes:**
- Permission Name, Active

---

#### V4__Create_user_roles_table.sql
Creates the `user_roles` junction table for many-to-many relationship between users and roles.

**Columns:**
- `id` - Primary key
- `user_id` - Foreign key to users table
- `role_id` - Foreign key to roles table
- `active` - Boolean flag
- `assigned_at` - Assignment timestamp

**Constraints:**
- Foreign keys with CASCADE DELETE
- Unique constraint on (user_id, role_id)

**Indexes:**
- User ID, Role ID, Active

---

#### V5__Create_role_permissions_table.sql
Creates the `role_permissions` junction table for many-to-many relationship between roles and permissions.

**Columns:**
- `id` - Primary key
- `role_id` - Foreign key to roles table
- `permission_id` - Foreign key to permissions table
- `active` - Boolean flag
- `assigned_at` - Assignment timestamp

**Constraints:**
- Foreign keys with CASCADE DELETE
- Unique constraint on (role_id, permission_id)

**Indexes:**
- Role ID, Permission ID, Active

---

#### V6__Create_sessions_table.sql
Creates the `sessions` table for tracking user sessions.

**Columns:**
- `id` - Primary key
- `user_id` - Foreign key to users table
- `session_token` - Unique session token
- `ip_address` - Client IP address
- `user_agent` - Client User Agent
- `login_at` - Login timestamp
- `last_activity_at` - Last activity timestamp
- `logout_at` - Logout timestamp (nullable)
- `active` - Boolean flag for session status

**Indexes:**
- User ID, Session Token, Active, Login At, (User ID, Active)

---

#### V7__Insert_sample_data.sql
Inserts sample data for testing and demonstration.

**Sample Data:**
- 3 Users (admin, john_doe, jane_smith)
- 3 Roles (ADMIN, USER, MANAGER)
- 6 Permissions (READ, WRITE, DELETE, MANAGE_USERS, MANAGE_ROLES, VIEW_REPORTS)
- User-Role assignments
- Role-Permission assignments

**Note:** Passwords are BCrypt hashed. The sample password is "password123" for all users.

---

## Configuration

### application.yaml
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baselineOnMigrate: true
    outOfOrder: false
```

**Settings:**
- `enabled: true` - Enable Flyway migrations
- `locations` - Directory containing migration scripts
- `baselineOnMigrate: true` - Create baseline on first run
- `outOfOrder: false` - Disallow out-of-order migrations

### Hibernate Configuration
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

**Important:** `ddl-auto` is set to `validate` to ensure Hibernate validates against the schema created by Flyway, rather than generating it.

---

## Running Migrations

### Automatic (on Application Start)
When the Spring Boot application starts, Flyway will:
1. Connect to the database
2. Check the `flyway_schema_history` table
3. Execute any pending migrations
4. Update the history table

### Manual (with Maven)
```bash
mvn flyway:migrate
```

### Flyway Commands

**Validate migration status:**
```bash
mvn flyway:info
```

**Clean database (WARNING: Destroys all data):**
```bash
mvn flyway:clean
```

**Repair migration history:**
```bash
mvn flyway:repair
```

---

## Flyway Schema History

Flyway maintains a `flyway_schema_history` table that tracks all executed migrations:

| installed_rank | version | description | type | script | checksum | installed_by | installed_on | execution_time | success |
|---|---|---|---|---|---|---|---|---|---|
| 1 | 1 | Create users table | SQL | V1__Create_users_table.sql | ... | root | ... | ... | true |
| 2 | 2 | Create roles table | SQL | V2__Create_roles_table.sql | ... | root | ... | ... | true |
| ... | ... | ... | ... | ... | ... | ... | ... | ... | ... |

---

## Best Practices

### 1. Version Control
- Keep all migration files in version control
- Review migration scripts before deployment
- Never modify executed migrations

### 2. Naming
- Use clear, descriptive names
- Use underscores instead of spaces
- Follow the V{version}__{description} pattern

### 3. Content
- One logical change per migration
- Use comments to explain complex changes
- Include indexes for performance

### 4. Testing
- Test migrations in development environment first
- Verify backward compatibility when possible
- Use sample data for testing

### 5. Deployment
- Review migration scripts in code review
- Test on staging environment
- Have rollback plan (may require new migration)

---

## Adding New Migrations

### Steps to Add a Migration

1. **Create new SQL file** with next version number:
   ```sql
   src/main/resources/db/migration/V8__Add_new_column.sql
   ```

2. **Write migration SQL:**
   ```sql
   -- V8__Add_new_column.sql
   ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);
   ```

3. **Build and run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Verify execution:**
   - Check logs for migration execution
   - Verify in database: `SELECT * FROM flyway_schema_history;`

---

## Common Scenarios

### Adding a Column
```sql
ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);
CREATE INDEX idx_phone ON users(phone_number);
```

### Renaming a Column
```sql
ALTER TABLE users CHANGE COLUMN full_name full_name_updated VARCHAR(255);
```

### Dropping a Column
```sql
ALTER TABLE users DROP COLUMN deprecated_field;
```

### Adding a Constraint
```sql
ALTER TABLE users ADD CONSTRAINT unique_phone UNIQUE (phone_number);
```

### Creating a New Table with Foreign Key
```sql
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    action VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## Troubleshooting

### Migration Failed
1. Check database connectivity
2. Review migration syntax
3. Check for conflicts with existing schema
4. Use `mvn flyway:repair` to fix checksum issues

### Out of Order Migration
- Migrations must be executed in version order
- Set `outOfOrder: true` only if absolutely necessary
- Consider creating new migrations instead

### Checksum Mismatch
```bash
# Repair the checksum
mvn flyway:repair
```

---

## Database Engine

All migrations use **InnoDB** storage engine with **utf8mb4** charset for:
- Better transaction support
- Referential integrity
- International character support

```sql
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
```

---

## Performance Considerations

- Indexes are created for frequently queried columns
- Foreign keys use CASCADE DELETE for referential integrity
- Unique constraints prevent duplicate data
- Composite indexes for multi-column queries

---

## Security

- Passwords are BCrypt hashed in sample data
- No sensitive data in migration scripts
- Database credentials in application.yaml only
- Consider using environment variables for production

---

## Summary

Flyway provides:
✅ Version control for database schema
✅ Reproducible deployments
✅ Automatic migration execution
✅ Migration history tracking
✅ Rollback capability (via new migrations)

For more information, visit: https://flywaydb.org/

---

**Last Updated:** March 3, 2026
**Version:** 1.0.0

