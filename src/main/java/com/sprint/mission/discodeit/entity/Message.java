package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID messageId;
    private final UUID userId;
    private final UUID channelId;
    private String content;
    private boolean active;
    private final Long createdAt;
    private Long updatedAt;

    public Message(String content, User user, Channel channel) {
        messageId = UUID.randomUUID();
        this.content = content;
        this.userId = user.getUserId();
        this.channelId = channel.getChannelId();
        this.active = true;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public UUID getMessageId() {
        return messageId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public String getContent() {
        return content;
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

    public void updateContent(String content) {
        this.content = content;
        updatedAt = System.currentTimeMillis();
    }

    public void updateActive(boolean active) {
        this.active = active;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", content='" + content + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}