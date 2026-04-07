package com.sprint.mission.discodeit.domain;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant readAt;
    private Instant createdAt;
    private Instant updatedAt;

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
