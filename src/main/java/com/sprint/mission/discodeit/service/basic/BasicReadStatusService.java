package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final ReadStatusMapper readStatusMapper;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    @Transactional
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException(
                        "User not found with id " + request.userId()
                ));

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Channel not found with id " + request.channelId()
                ));

        if (readStatusRepository.findByUser_IdAndChannel_Id(
                request.userId(),
                request.channelId()
        ).isPresent()) {
            throw new IllegalStateException(
                    "Read status already exists for user id " + request.userId()
                            + " and channel id " + request.channelId()
            );
        }

        ReadStatus readStatus = readStatusMapper.toEntity(request, user, channel);
        ReadStatus saved = readStatusRepository.save(readStatus);

        return readStatusMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadStatusResponse findById(UUID id) {
        ReadStatus readStatus = getReadStatusOrThrow(id);
        return readStatusMapper.toResponse(readStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUser_Id(userId).stream()
                .map(readStatusMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = getReadStatusOrThrow(readStatusId);

        readStatus.updateLastReadAt(request.newLastReadAt());

        return readStatusMapper.toResponse(readStatus);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ReadStatus readStatus = getReadStatusOrThrow(id);
        readStatusRepository.delete(readStatus);
    }

    private ReadStatus getReadStatusOrThrow(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException(
                        "ReadStatus with id " + readStatusId + " not found"
                ));
    }
}