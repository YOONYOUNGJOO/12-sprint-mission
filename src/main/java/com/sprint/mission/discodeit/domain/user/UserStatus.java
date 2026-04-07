package com.sprint.mission.discodeit.domain.user;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private UUID id;
    private UUID userId;
    private Instant lastActiveAt;
    private Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastActiveAt = Instant.now();

        this.userId = userId;

    }

    public void update(Instant lastActiveAt){
        this.lastActiveAt = lastActiveAt;
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        return this.lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "id=" + id +
                ", userId=" + userId +
                ", lastActiveAt=" + lastActiveAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

