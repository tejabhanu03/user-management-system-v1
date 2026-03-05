package com.use_management_system.user_management.service;

import com.use_management_system.user_management.entity.Session;
import com.use_management_system.user_management.entity.User;
import com.use_management_system.user_management.repository.SessionRepository;
import com.use_management_system.user_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private UUID userId;
    private UUID sessionId;
    private User testUser;
    private Session testSession;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setActive(true);

        testSession = new Session();
        testSession.setId(sessionId);
        testSession.setUser(testUser);
        testSession.setSessionToken("sessionToken123");
        testSession.setIpAddress("127.0.0.1");
        testSession.setUserAgent("test-agent");
        testSession.setActive(true);
    }

    @Test
    void testGetUserActiveSessionsSuccess() {
        // Arrange
        Session session1 = new Session();
        session1.setId(UUID.randomUUID());
        session1.setUser(testUser);
        session1.setActive(true);

        Session session2 = new Session();
        session2.setId(UUID.randomUUID());
        session2.setUser(testUser);
        session2.setActive(true);

        List<Session> activeSessions = Arrays.asList(session1, session2);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(sessionRepository.findByUserAndActive(testUser, true)).thenReturn(activeSessions);

        // Act
        List<Session> sessions = sessionService.getUserActiveSessions(userId);

        // Assert
        assertNotNull(sessions);
        assertEquals(2, sessions.size());
        assertTrue(sessions.stream().allMatch(Session::getActive));

        verify(userRepository, times(1)).findById(userId);
        verify(sessionRepository, times(1)).findByUserAndActive(testUser, true);
    }

    @Test
    void testGetUserActiveSessionsEmpty() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(sessionRepository.findByUserAndActive(testUser, true)).thenReturn(Collections.emptyList());

        // Act
        List<Session> sessions = sessionService.getUserActiveSessions(userId);

        // Assert
        assertNotNull(sessions);
        assertEquals(0, sessions.size());

        verify(userRepository, times(1)).findById(userId);
        verify(sessionRepository, times(1)).findByUserAndActive(testUser, true);
    }

    @Test
    void testGetUserActiveSessionsUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> sessionService.getUserActiveSessions(userId));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(sessionRepository, never()).findByUserAndActive(any(), any());
    }

    @Test
    void testGetUserAllSessionsSuccess() {
        // Arrange
        Session activeSession = new Session();
        activeSession.setId(UUID.randomUUID());
        activeSession.setUser(testUser);
        activeSession.setActive(true);

        Session inactiveSession = new Session();
        inactiveSession.setId(UUID.randomUUID());
        inactiveSession.setUser(testUser);
        inactiveSession.setActive(false);

        List<Session> allSessions = Arrays.asList(activeSession, inactiveSession);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(sessionRepository.findByUser(testUser)).thenReturn(allSessions);

        // Act
        List<Session> sessions = sessionService.getUserAllSessions(userId);

        // Assert
        assertNotNull(sessions);
        assertEquals(2, sessions.size());

        verify(userRepository, times(1)).findById(userId);
        verify(sessionRepository, times(1)).findByUser(testUser);
    }

    @Test
    void testGetUserAllSessionsEmpty() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(sessionRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        // Act
        List<Session> sessions = sessionService.getUserAllSessions(userId);

        // Assert
        assertNotNull(sessions);
        assertEquals(0, sessions.size());

        verify(userRepository, times(1)).findById(userId);
        verify(sessionRepository, times(1)).findByUser(testUser);
    }

    @Test
    void testGetUserAllSessionsUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> sessionService.getUserAllSessions(userId));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(sessionRepository, never()).findByUser(any());
    }

    @Test
    void testGetSessionByTokenSuccess() {
        // Arrange
        when(sessionRepository.findBySessionToken("sessionToken123"))
            .thenReturn(Optional.of(testSession));

        // Act
        Session session = sessionService.getSessionByToken("sessionToken123");

        // Assert
        assertNotNull(session);
        assertEquals("sessionToken123", session.getSessionToken());
        assertEquals(userId, session.getUser().getId());

        verify(sessionRepository, times(1)).findBySessionToken("sessionToken123");
    }

    @Test
    void testGetSessionByTokenNotFound() {
        // Arrange
        when(sessionRepository.findBySessionToken("invalidToken"))
            .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> sessionService.getSessionByToken("invalidToken"));
        assertEquals("Session not found", exception.getMessage());

        verify(sessionRepository, times(1)).findBySessionToken("invalidToken");
    }

    @Test
    void testInvalidateAllUserSessionsSuccess() {
        // Arrange
        Session activeSession1 = new Session();
        activeSession1.setId(UUID.randomUUID());
        activeSession1.setUser(testUser);
        activeSession1.setActive(true);
        activeSession1.setLogoutAt(null);

        Session activeSession2 = new Session();
        activeSession2.setId(UUID.randomUUID());
        activeSession2.setUser(testUser);
        activeSession2.setActive(true);
        activeSession2.setLogoutAt(null);

        List<Session> activeSessions = Arrays.asList(activeSession1, activeSession2);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(sessionRepository.findByUserAndActive(testUser, true)).thenReturn(activeSessions);
        when(sessionRepository.saveAll(any())).thenReturn(activeSessions);

        // Act
        sessionService.invalidateAllUserSessions(userId);

        // Assert
        ArgumentCaptor<List<Session>> captor = ArgumentCaptor.forClass(List.class);
        verify(sessionRepository, times(1)).saveAll(captor.capture());

        List<Session> savedSessions = captor.getValue();
        assertFalse(savedSessions.get(0).getActive());
        assertFalse(savedSessions.get(1).getActive());
        assertNotNull(savedSessions.get(0).getLogoutAt());
        assertNotNull(savedSessions.get(1).getLogoutAt());

        verify(userRepository, times(1)).findById(userId);
        verify(sessionRepository, times(1)).findByUserAndActive(testUser, true);
    }

    @Test
    void testInvalidateAllUserSessionsEmpty() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(sessionRepository.findByUserAndActive(testUser, true)).thenReturn(Collections.emptyList());

        // Act
        sessionService.invalidateAllUserSessions(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(sessionRepository, times(1)).findByUserAndActive(testUser, true);
        verify(sessionRepository, times(1)).saveAll(Collections.emptyList());
    }

    @Test
    void testInvalidateAllUserSessionsUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> sessionService.invalidateAllUserSessions(userId));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(sessionRepository, never()).findByUserAndActive(any(), any());
        verify(sessionRepository, never()).saveAll(any());
    }

    @Test
    void testUpdateSessionActivitySuccess() {
        // Arrange
        LocalDateTime beforeUpdate = LocalDateTime.now();
        when(sessionRepository.findBySessionToken("sessionToken123"))
            .thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        // Act
        sessionService.updateSessionActivity("sessionToken123");

        // Assert
        assertNotNull(testSession.getLastActivityAt());
        assertTrue(testSession.getLastActivityAt().isAfter(beforeUpdate) ||
                   testSession.getLastActivityAt().isEqual(beforeUpdate));

        verify(sessionRepository, times(1)).findBySessionToken("sessionToken123");
        verify(sessionRepository, times(1)).save(testSession);
    }

    @Test
    void testUpdateSessionActivitySessionNotFound() {
        // Arrange
        when(sessionRepository.findBySessionToken("invalidToken"))
            .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> sessionService.updateSessionActivity("invalidToken"));
        assertEquals("Session not found", exception.getMessage());

        verify(sessionRepository, times(1)).findBySessionToken("invalidToken");
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void testUpdateSessionActivityMultipleTimes() {
        // Arrange
        when(sessionRepository.findBySessionToken("sessionToken123"))
            .thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        // Act
        sessionService.updateSessionActivity("sessionToken123");
        LocalDateTime firstUpdate = testSession.getLastActivityAt();

        // Wait a moment to ensure time difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        sessionService.updateSessionActivity("sessionToken123");
        LocalDateTime secondUpdate = testSession.getLastActivityAt();

        // Assert
        assertNotNull(firstUpdate);
        assertNotNull(secondUpdate);
        // Second update should be at the same time or later
        assertTrue(secondUpdate.isEqual(firstUpdate) || secondUpdate.isAfter(firstUpdate));

        verify(sessionRepository, times(2)).findBySessionToken("sessionToken123");
        verify(sessionRepository, times(2)).save(any(Session.class));
    }
}

