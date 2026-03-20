package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final

    @Override
    public Channel save(Channel channel) {

    }

    @Override
    public Channel findById(UUID id) {
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return List.of();
    }

    @Override
    public Channel updateChannelName(UUID id, String channelName) {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public Channel softDeleteById(UUID id) {
        return null;
    }
}
