package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.domain.user.UserStatus;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponse create(UserCreateRequest dto) {
        boolean existsUsername = userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(dto.username()));
        if (existsUsername) {
            throw new IllegalArgumentException("Username already exists: " + dto.username());
        }

        boolean existsEmail = userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(dto.email()));
        if (existsEmail) {
            throw new IllegalArgumentException("Email already exists: " + dto.email());
        }

        User user;
        if (dto.profile() != null) {
            BinaryContent binaryContent = new BinaryContent(dto.profile().data(), dto.profile().filename(), dto.profile().mimeType());
            binaryContentRepository.save(binaryContent);
            user = new User(binaryContent.getId(), dto.username(), dto.email(), dto.password());
        } else {
            user = new User(dto.username(), dto.email(), dto.password());
        }

        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);
        userRepository.save(user);

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
               user.getCreatedAt(), userStatus.isOnline());
    }

    @Override
    public UserResponse findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("No user status found for user id " + userId));

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getCreatedAt(), userStatus.isOnline());
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new NoSuchElementException(
                                    "No user status found for user id " + user.getId()
                            ));
                    return new UserResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getCreatedAt(),
                            userStatus.isOnline()

                    );
                })
                .toList();
    }

    @Override
    public UserResponse update(UserUpdateRequest dto) {
        if (dto.newUsername() != null) {
            boolean existsUsername = userRepository.findAll().stream()
                    .anyMatch(user -> !user.getId().equals(dto.userId())
                            && user.getUsername().equals(dto.newUsername()));
            if (existsUsername) {
                throw new IllegalArgumentException("Username already exists: " + dto.newUsername());
            }
        }

        if (dto.newEmail() != null) {
            boolean existsEmail = userRepository.findAll().stream()
                    .anyMatch(user -> !user.getId().equals(dto.userId())
                            && user.getEmail().equals(dto.newEmail()));
            if (existsEmail) {
                throw new IllegalArgumentException("Email already exists: " + dto.newEmail());
            }
        }

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + dto.userId() + " not found"));

        if (dto.newProfile() != null) {
            if (user.getProfileId() != null) {
                binaryContentRepository.deleteById(user.getProfileId());
            }
            BinaryContent binaryContent = new BinaryContent(dto.newProfile().data(), dto.newProfile().filename(), dto.newProfile().mimeType());
            binaryContentRepository.save(binaryContent);
            user.update(dto.newUsername(), dto.newEmail(), dto.newPassword(), binaryContent.getId());
        } else {
            user.update(dto.newUsername(), dto.newEmail(), dto.newPassword(), user.getProfileId());
        }

        userRepository.save(user);

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("No user status found for user id " + user.getId()));

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(),user.getCreatedAt(), userStatus.isOnline());
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("No user status found for user id " + userId));

        userRepository.deleteById(user.getId());
        userStatusRepository.deleteById(userStatus.getId());

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
    }
}