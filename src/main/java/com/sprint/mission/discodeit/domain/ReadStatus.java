package com.sprint.mission.discodeit.domain;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant readAt;

    private final UUID userId;
    private final UUID channelId;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.readAt = Instant.now();

        this.userId = userId;
        this.channelId = channelId;

    }

    public void update(Instant lastReadAt){
        this.readAt = lastReadAt;
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "ReadStatus{" +
                "id=" + id +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", readAt=" + readAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
