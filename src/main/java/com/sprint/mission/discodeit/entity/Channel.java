package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID channelId;
    private final UUID ownerId;
    private String channelName;
    private final Long createdAt;
    private Long updatedAt;

    public Channel(String channelName, User user) {
        channelId = UUID.randomUUID();
        this.ownerId = user.getUserId();
        this.channelName = channelName;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getChannelName() {
        return channelName;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelId=" + channelId +
                ", ownerId=" + ownerId +
                ", channelName='" + channelName + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}