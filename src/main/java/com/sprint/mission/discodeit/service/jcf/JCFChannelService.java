package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;
    public JCFChannelService() {
        data = new ArrayList<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.add(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return data.stream().filter(channel -> channel.getChannelId().equals(id)).findFirst().orElseThrow();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Channel updateChannelName(UUID id, String channelName) {
        Channel ch = findById(id);
        ch.updateChannelName(channelName);
        return ch;
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(findById(id));
    }

    @Override
    public Channel softDeleteById(UUID id) {
        Channel ch = findById(id);
        ch.updateActive(false);
        return ch;
    }
}
