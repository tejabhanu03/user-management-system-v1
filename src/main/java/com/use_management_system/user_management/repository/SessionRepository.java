package com.use_management_system.user_management.repository;

import com.use_management_system.user_management.entity.Session;
import com.use_management_system.user_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    Optional<Session> findBySessionToken(String sessionToken);
    List<Session> findByUserAndActive(User user, Boolean active);
    List<Session> findByUser(User user);
}
