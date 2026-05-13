package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserStatusRepository userStatusRepository;
    private final UserStatusMapper userStatusMapper;
    private final BinaryContentService binaryContentService;

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request,
                               Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists: " + request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        BinaryContent profile = optionalProfileCreateRequest
                .map(binaryContentService::createBinaryContent)
                .orElse(null);

        User user = userMapper.toEntity(request, profile);
        User userSaved = userRepository.save(user);

        UserStatus userStatus = userStatusMapper.toEntity(userSaved, Instant.now());
        UserStatus userStatusSaved = userStatusRepository.save(userStatus);

        return userMapper.toResponse(userSaved, userStatusSaved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("No user status found for user id " + userId));

        return userMapper.toResponse(user, userStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findByUser_Id(user.getId())
                            .orElseThrow(() -> new NoSuchElementException(
                                    "No user status found for user id " + user.getId()
                            ));

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        if (request.newUsername() != null
                && !request.newUsername().equals(user.getUsername())
                && userRepository.existsByUsername(request.newUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.newUsername());
        }

        if (request.newEmail() != null
                && !request.newEmail().equals(user.getEmail())
                && userRepository.existsByEmail(request.newEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.newEmail());
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

        UserStatus userStatus = userStatusRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NoSuchElementException("No user status found for user id " + user.getId()));

        return userMapper.toResponse(user, userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("No user status found for user id " + userId));

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
}