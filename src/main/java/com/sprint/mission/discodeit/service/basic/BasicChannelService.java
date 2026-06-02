package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.channel.ChannelType;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ReadStatusMapper readStatusMapper;
    private final MessageRepository messageRepository;
    private final MessageService messageService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ChannelResponse createPublicChannel(CreatePublicChannelRequest request) {
        Channel channel = Channel.createPublic(request.name(), request.description());
        Channel saved = channelRepository.save(channel);

        return channelMapper.toResponse(saved, null, List.of());
    }

    @Override
    @Transactional
    public ChannelResponse createPrivateChannel(CreatePrivateChannelRequest request) {
        Channel channel = Channel.createPrivate();
        Channel saved = channelRepository.save(channel);

        Instant now = Instant.now();
        List<UserResponse> participants = new ArrayList<>();

        for (UUID userId : request.participantIds()) {
            User user = getUserOrThrow(userId);

            ReadStatus readStatus = readStatusMapper.toEntity(user, saved, now);
            readStatusRepository.save(readStatus);

            participants.add(userMapper.toResponse(user));
        }

        return channelMapper.toResponse(saved, null, participants);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelResponse findById(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);

        return toResponse(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        User user = getUserOrThrow(userId);

        List<UUID> participatedChannelIds = readStatusRepository.findAllByUser_Id(user.getId()).stream()
                .map(readStatus -> readStatus.getChannel().getId())
                .toList();

        List<Channel> channels = channelRepository.findAllByTypeOrIdIn(
                ChannelType.PUBLIC,
                participatedChannelIds
        );

        return channels.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChannelResponse update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = getChannelOrThrow(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
           throw new PrivateChannelUpdateNotAllowedException(channelId);
        }

        channel.update(request.newName(), request.newDescription());

        return toResponse(channel);
    }

    @Override
    @Transactional
    public void delete(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);

        List<Message> messages = messageRepository.findAllByChannel_Id(channelId);
        for (Message message : messages) {
            messageService.delete(message.getId());
        }

        channelRepository.delete(channel);
    }

    private ChannelResponse toResponse(Channel channel) {
        Instant latestMessageAt = getLatestMessageAt(channel.getId());
        List<UserResponse> participants = findParticipants(channel);

        return channelMapper.toResponse(channel, latestMessageAt, participants);
    }

    private List<UserResponse> findParticipants(Channel channel) {
        if (channel.getType() == ChannelType.PUBLIC) {
            return List.of();
        }

        return readStatusRepository.findAllByChannelIdWithUser(channel.getId()).stream()
                .map(ReadStatus::getUser)
                .map(userMapper::toResponse)
                .toList();
    }

    private Instant getLatestMessageAt(UUID channelId) {
        return messageRepository.findFirstByChannel_IdOrderByCreatedAtDesc(channelId)
                .map(Message::getCreatedAt)
                .orElse(null);
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}