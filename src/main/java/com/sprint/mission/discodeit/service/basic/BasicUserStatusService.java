package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusResponse create(UserStatusCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id " + request.userId()));

        if (userStatusRepository.findByUser_Id(request.userId()).isPresent()) {
            throw new IllegalStateException("User status already exists for user id " + request.userId());
        }

        UserStatus userStatus = userStatusMapper.toEntity(user, request.lastActiveAt());
        UserStatus saved = userStatusRepository.save(userStatus);

        return userStatusMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatusResponse findById(UUID id) {
        UserStatus userStatus = getUserStatusOrThrow(id);

        return userStatusMapper.toResponse(userStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserStatusResponse update(UUID id, UserStatusUpdateRequest request) {
        UserStatus userStatus = getUserStatusOrThrow(id);

        userStatus.updateLastActiveAt(request.newLastActiveAt());

        return userStatusMapper.toResponse(userStatus);
    }

    @Override
    @Transactional
    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("No user status found for user id " + userId));

        userStatus.updateLastActiveAt(request.newLastActiveAt());

        return userStatusMapper.toResponse(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UserStatus userStatus = getUserStatusOrThrow(id);

        userStatusRepository.delete(userStatus);
    }

    private UserStatus getUserStatusOrThrow(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
    }
}