# Flyway Quick Reference

## Quick Start

```bash
# 1. Create database
mysql -u root -p pass
> CREATE DATABASE user_management;

# 2. Build project (downloads Flyway)
mvn clean install

# 3. Run app (Flyway executes migrations)
mvn spring-boot:run

# 4. Verify migrations
mysql -u root -p pass user_management
> SELECT * FROM flyway_schema_history;
```

---

## Migration Files

Located in: `src/main/resources/db/migration/`

```
V1__Create_users_table.sql          ← Creates users
V2__Create_roles_table.sql          ← Creates roles
V3__Create_permissions_table.sql    ← Creates permissions
V4__Create_user_roles_table.sql     ← Links users & roles
V5__Create_role_permissions_table.sql ← Links roles & permissions
V6__Create_sessions_table.sql       ← Creates sessions
V7__Insert_sample_data.sql          ← Sample data
```

---

## Flyway Commands

```bash
# Check migration status
mvn flyway:info

# Repair migration history
mvn flyway:repair

# Validate migrations
mvn flyway:validate

# Clean database (⚠️ DELETES ALL DATA)
mvn flyway:clean

# Migrate database
mvn flyway:migrate
```

---

## Configuration (application.yaml)

```yaml
spring:
  flyway:
    enabled: true                    # Enable Flyway
    locations: classpath:db/migration # Migration location
    baselineOnMigrate: true          # Create baseline
    outOfOrder: false                # No out-of-order
  jpa:
    hibernate:
      ddl-auto: validate             # Validate only
```

---

## Adding New Migration

### Step 1: Create file
```
src/main/resources/db/migration/V8__Description.sql
```

### Step 2: Write migration
```sql
-- V8__Add_column_to_users.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
CREATE INDEX idx_phone ON users(phone);
```

### Step 3: Run
```bash
mvn spring-boot:run  # Auto-executes new migrations
```

---

## Sample Data

### Users (all password: password123)
- admin (ADMIN role)
- john_doe (USER role)
- jane_smith (MANAGER role)

### Roles
- ADMIN (all permissions)
- USER (READ, VIEW_REPORTS)
- MANAGER (READ, WRITE, MANAGE_USERS, VIEW_REPORTS)

### Permissions
- READ
- WRITE
- DELETE
- MANAGE_USERS
- MANAGE_ROLES
- VIEW_REPORTS

---

## Database Tables

| Table | Purpose |
|-------|---------|
| users | User accounts |
| roles | User roles |
| permissions | System permissions |
| user_roles | User-Role mapping |
| role_permissions | Role-Permission mapping |
| sessions | User sessions |
| flyway_schema_history | Migration history |

---

## Important Notes

✅ Migrations run in order (V1, V2, V3...)
✅ Never modify executed migrations
✅ Create new migrations for changes
✅ Flyway auto-runs on app start
✅ Check logs for migration status
✅ Sample data included in V7

---

## Troubleshooting

### Migrations not running?
- Check database connection
- Verify credentials in application.yaml
- Check flyway config enabled: true
- Look at application logs

### Checksum error?
```bash
mvn flyway:repair
```

### View executed migrations
```sql
SELECT * FROM flyway_schema_history;
```

### View table structure
```sql
DESC users;
SHOW CREATE TABLE users;
```

---

## Development Workflow

1. **Write migration** → `V8__Your_change.sql`
2. **Place in** → `src/main/resources/db/migration/`
3. **Run app** → `mvn spring-boot:run`
4. **Check logs** → Flyway migration executed
5. **Verify** → Query database to confirm

---

## Common Migrations

### Add Column
```sql
ALTER TABLE users ADD COLUMN middle_name VARCHAR(100);
```

### Rename Column
```sql
ALTER TABLE users CHANGE COLUMN full_name full_name_updated VARCHAR(255);
```

### Drop Column
```sql
ALTER TABLE users DROP COLUMN deprecated_field;
```

### Add Index
```sql
CREATE INDEX idx_email ON users(email);
```

### Add Constraint
```sql
ALTER TABLE users ADD CONSTRAINT unique_email UNIQUE (email);
```

### Create New Table
```sql
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    action VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## Status Check

```bash
# See migration history
mvn flyway:info

# Output example:
# +---------+---------+---------------------+------+-----+----------+----------+
# | Version | Description | Installed On | State |  ...
# +---------+---------+---------------------+------+-----+----------+----------+
# | 1 | Create users table | 2026-03-03 21:00:00 | SUCCESS |  ...
# | 2 | Create roles table | 2026-03-03 21:00:01 | SUCCESS |  ...
# ...
```

---

## Key Files

- **pom.xml** - Flyway dependencies
- **application.yaml** - Flyway config
- **db/migration/** - Migration scripts
- **FLYWAY_MIGRATIONS_GUIDE.md** - Detailed guide
- **FLYWAY_SETUP_COMPLETE.md** - Setup summary

---

## Environment Variables

For production, use environment variables instead of hardcoding:

```bash
# Set in environment
export SPRING_DATASOURCE_URL=jdbc:mysql://prod-server:3306/user_management
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=prod_password

# Then run
mvn spring-boot:run
```

---

## Documentation

- **FLYWAY_MIGRATIONS_GUIDE.md** - Complete guide (detailed)
- **FLYWAY_SETUP_COMPLETE.md** - Setup summary (overview)
- **FLYWAY_QUICK_REFERENCE.md** - This file (quick)

---

**Ready to use Flyway? Run: `mvn spring-boot:run`**

See **FLYWAY_MIGRATIONS_GUIDE.md** for complete documentation.

