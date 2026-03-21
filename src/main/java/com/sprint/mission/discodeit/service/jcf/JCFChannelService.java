package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    public JCFChannelService() {
        data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getChannelId() ,channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        if(data.get(id) == null){
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels =  new ArrayList<>(data.values());
        channels.sort((c1, c2) -> Long.compare(c1.getCreatedAt(), c2.getCreatedAt()));
        return channels;
    }

    @Override
    public Channel updateChannelName(UUID id, String channelName) {
        Channel ch = findById(id);
        ch.updateChannelName(channelName);
        return ch;
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

}
