package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.channel.ChannelType;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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

        for (UUID userId : request.participantIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found with id " + userId));

            ReadStatus readStatus = readStatusMapper.toEntity(user, saved, Instant.MIN);
            readStatusRepository.save(readStatus);
        }

        return channelMapper.toResponse(saved, null, request.participantIds());
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelResponse findById(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);

        Instant latestMessageAt = getLatestMessageAt(channel.getId());

        List<UUID> participantUserIds = List.of();

        if (channel.getType() == ChannelType.PRIVATE) {
            participantUserIds = readStatusRepository.findAllByChannel_Id(channelId).stream()
                    .map(readStatus -> readStatus.getUser().getId())
                    .toList();
        }

        return channelMapper.toResponse(channel, latestMessageAt, participantUserIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<Channel> channels = channelRepository.findAll();
        List<ChannelResponse> responses = new ArrayList<>();

        for (Channel channel : channels) {
            Instant latestMessageAt = getLatestMessageAt(channel.getId());

            if (channel.getType() == ChannelType.PUBLIC) {
                responses.add(channelMapper.toResponse(channel, latestMessageAt, List.of()));
                continue;
            }

            if (readStatusRepository.findByUser_IdAndChannel_Id(userId, channel.getId()).isPresent()) {
                List<UUID> participantUserIds = readStatusRepository.findAllByChannel_Id(channel.getId()).stream()
                        .map(readStatus -> readStatus.getUser().getId())
                        .toList();

                responses.add(channelMapper.toResponse(channel, latestMessageAt, participantUserIds));
            }
        }

        return responses;
    }

    @Override
    @Transactional
    public ChannelResponse update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = getChannelOrThrow(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("Private channel cannot be updated");
        }

        Instant latestMessageAt = getLatestMessageAt(channel.getId());

        channel.update(request.newName(), request.newDescription());

        return channelMapper.toResponse(channel, latestMessageAt, List.of());
    }

    @Override
    @Transactional
    public void delete(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);
        List<Message> messages = messageRepository.findAllByChannel_Id(channelId);
        for (Message message : messages) {
            messageService.delete(message.getId());
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel_Id(channelId);
        readStatusRepository.deleteAll(readStatuses);

        channelRepository.delete(channel);
    }

    private Instant getLatestMessageAt(UUID channelId) {
        return messageRepository.findAllByChannel_Id(channelId).stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }
}