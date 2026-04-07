package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.channel.Channel;
import com.sprint.mission.discodeit.domain.channel.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelType type, String name, String description);
    Channel find(UUID channelId);
    List<Channel> findAll();
    Channel update(UUID channelId, String newName, String newDescription);
    void delete(UUID channelId);
}
