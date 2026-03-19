package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID channelId;
    private final UUID ownerId;
    private String channelName;
    private boolean active;
    private final Long createdAt;
    private Long updatedAt;

    public Channel(String channelName, User user) {
        channelId = UUID.randomUUID();
        this.ownerId = user.getUserId();
        this.channelName = channelName;
        this.active = true;
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

    public boolean isActive() {
        return active;
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

    public void updateActive(boolean active) {
        this.active = active;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelId=" + channelId +
                ", ownerId=" + ownerId +
                ", channelName='" + channelName + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}