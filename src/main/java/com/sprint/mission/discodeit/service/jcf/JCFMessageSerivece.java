package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class JCFMessageSerivece implements MessageService {
    @Override
    public Message save(Message message) {
        return null;
    }

    @Override
    public Message findById(UUID id) {
        return null;
    }

    @Override
    public List<Message> findAll() {
        return List.of();
    }

    @Override
    public Message updateContent(UUID id, String content) {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public Message softDeleteById(UUID id) {
        return null;
    }
}
