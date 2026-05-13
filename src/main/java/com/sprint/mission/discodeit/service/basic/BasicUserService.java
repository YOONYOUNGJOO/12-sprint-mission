package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final UserStatusMapper userStatusMapper;
    private final BinaryContentService binaryContentService;

    @Override
    @Transactional
    public UserResponse create(
            UserCreateRequest request,
            Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
    ) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException(
                    "User with username " + request.username() + " already exists"
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "User with email " + request.email() + " already exists"
            );
        }

        BinaryContent profile = optionalProfileCreateRequest
                .map(binaryContentService::createBinaryContent)
                .orElse(null);

        User user = userMapper.toEntity(request, profile);
        User savedUser = userRepository.save(user);

        UserStatus userStatus = userStatusMapper.toEntity(savedUser, Instant.now());
        UserStatus savedUserStatus = userStatusRepository.save(userStatus);

        return userMapper.toResponse(savedUser, savedUserStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(UUID userId) {
        User user = getUserOrThrow(userId);
        UserStatus userStatus = getUserStatusOrThrow(userId);

        return userMapper.toResponse(user, userStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = getUserStatusOrThrow(user.getId());
                    return userMapper.toResponse(user, userStatus);
                })
                .toList();
    }

    @Override
    @Transactional
    public UserResponse update(
            UUID userId,
            UserUpdateRequest request,
            Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
    ) {
        User user = getUserOrThrow(userId);

        if (request.newUsername() != null
                && !request.newUsername().equals(user.getUsername())
                && userRepository.existsByUsername(request.newUsername())) {
            throw new IllegalArgumentException(
                    "User with username " + request.newUsername() + " already exists"
            );
        }

        if (request.newEmail() != null
                && !request.newEmail().equals(user.getEmail())
                && userRepository.existsByEmail(request.newEmail())) {
            throw new IllegalArgumentException(
                    "User with email " + request.newEmail() + " already exists"
            );
        }

        BinaryContent oldProfile = user.getProfile();

        BinaryContent newProfile = optionalProfileCreateRequest
                .map(binaryContentService::createBinaryContent)
                .orElse(null);

        if (newProfile != null && oldProfile != null) {
            user.clearProfile();
        }

        user.update(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                newProfile
        );

        if (newProfile != null && oldProfile != null) {
            binaryContentService.delete(oldProfile.getId());
        }

        UserStatus userStatus = getUserStatusOrThrow(user.getId());

        return userMapper.toResponse(user, userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        User user = getUserOrThrow(userId);
        UserStatus userStatus = getUserStatusOrThrow(userId);
        BinaryContent profile = user.getProfile();

        if (profile != null) {
            user.clearProfile();
        }

        userStatusRepository.delete(userStatus);
        userRepository.delete(user);

        if (profile != null) {
            binaryContentService.delete(profile.getId());
        }
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id " + userId + " not found"
                ));
    }

    private UserStatus getUserStatusOrThrow(UUID userId) {
        return userStatusRepository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserStatus with userId " + userId + " not found"
                ));
    }
}