package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data;

    public JCFMessageService() {
        data = new ArrayList<>();
    }

    @Override
    public Message save(Message message) {
        data.add(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return data.stream().filter(message -> message.getMessageId().equals(id)).findFirst().orElseThrow();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Message updateContent(UUID id, String content) {
        Message message = findById(id);
        message.updateContent(content);
        return message;
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(findById(id));
    }

    @Override
    public Message softDeleteById(UUID id) {
        Message message = findById(id);
        message.updateActive(false);
        return message;
    }
}
