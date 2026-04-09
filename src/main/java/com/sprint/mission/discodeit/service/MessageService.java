package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(MessageCreateRequest dto);
    List<MessageResponse> findAllByChannelId(UUID channelId);
    MessageResponse update(MessageUpdateRequest dto);
    void delete(UUID messageId);
}
