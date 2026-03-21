package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private UserService us;
    private ChannelService cs;

    public JCFMessageService(UserService us, ChannelService cs) {
        data = new HashMap<>();
        this.us = us;
        this.cs = cs;
    }

    @Override
    public Message save(Message message) {
        if (!us.findAll().stream().anyMatch(user -> user.getUserId().equals(message.getUserId()))) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다");
        }
        if (!cs.findAll().stream().anyMatch(channel -> channel.getChannelId().equals(message.getChannelId()))) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다");
        }
        data.put(message.getMessageId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        if (data.get(id) == null) {
            throw new IllegalArgumentException("존재하지 않는 메세지 입니다.");
        }
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>(data.values());
        messages.sort((m1, m2) -> Long.compare(m1.getCreatedAt(), m2.getCreatedAt()));
        return messages;
    }

    @Override
    public Message updateContent(UUID id, String content) {
        Message message = findById(id);
        message.updateContent(content);
        return message;
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

}
