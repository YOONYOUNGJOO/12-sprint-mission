package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentService binaryContentService;

    @Override
    @Transactional
    public MessageResponse create(
            MessageCreateRequest request,
            List<BinaryContentCreateRequest> binaryContentCreateRequests
    ) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel not found with id " + request.channelId()));

        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new NoSuchElementException("Author not found with id " + request.authorId()));

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(binaryContentService::createBinaryContent)
                .toList();

        Message message = messageMapper.toEntity(request, channel, author, attachments);
        Message saved = messageRepository.save(message);

        return messageMapper.toResponse(saved, getAttachmentIds(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public Message find(UUID messageId) {
        return getMessageOrThrow(messageId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannel_Id(channelId).stream()
                .map(message -> messageMapper.toResponse(message, getAttachmentIds(message)))
                .toList();
    }

    @Override
    @Transactional
    public MessageResponse update(UUID messageId, MessageUpdateRequest request) {
        Message message = getMessageOrThrow(messageId);

        message.updateContent(request.newContent());

        return messageMapper.toResponse(message, getAttachmentIds(message));
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        Message message = getMessageOrThrow(messageId);
        List<UUID> attachmentIds = getAttachmentIds(message);

        message.clearAttachments();
        messageRepository.delete(message);

        for (UUID attachmentId : attachmentIds) {
            binaryContentService.delete(attachmentId);
        }
    }

    private Message getMessageOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    private List<UUID> getAttachmentIds(Message message) {
        return message.getAttachments().stream()
                .map(BinaryContent::getId)
                .toList();
    }
}