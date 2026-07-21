package com.sprint.mission.discodeit.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final SessionRegistry sessionRegistry;

    public boolean isOnline(UUID userId) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(DiscodeitUserDetails.class::isInstance)
                .map(DiscodeitUserDetails.class::cast)
                .filter(userDetails -> userDetails.getUserResponse().id().equals(userId))
                .flatMap(userDetails -> sessionRegistry.getAllSessions(userDetails, false).stream())
                .anyMatch(session -> !session.isExpired());
    }

    public void expireSessions(UUID userId) {
        sessionRegistry.getAllPrincipals().stream()
                .filter(DiscodeitUserDetails.class::isInstance)
                .map(DiscodeitUserDetails.class::cast)
                .filter(userDetails -> userDetails.getUserResponse().id().equals(userId))
                .flatMap(userDetails -> sessionRegistry.getAllSessions(userDetails, false).stream())
                .forEach(SessionInformation::expireNow);
    }
}
