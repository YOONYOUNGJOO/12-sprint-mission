package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.service.BinaryContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserStatusMapper userStatusMapper;

    @Mock
    private BinaryContentService binaryContentService;

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("사용자 생성 성공 - 프로필 없음")
    void create_success_withoutProfile() {
        UserCreateRequest request = new UserCreateRequest(
                "user1",
                "user1@test.com",
                "password"
        );

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        User savedUser = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        UserStatus userStatus = UserStatus.builder()
                .user(savedUser)
                .lastActiveAt(Instant.now())
                .build();

        UserResponse expectedResponse = new UserResponse(
                userId,
                "user1",
                "user1@test.com",
                null,
                true
        );

        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@test.com")).thenReturn(false);
        when(userMapper.toEntity(request, null)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userStatusMapper.toEntity(any(User.class), any(Instant.class))).thenReturn(userStatus);
        when(userStatusRepository.save(userStatus)).thenReturn(userStatus);
        when(userMapper.toResponse(savedUser, userStatus)).thenReturn(expectedResponse);

        UserResponse result = userService.create(request, Optional.empty());

        assertThat(result).isEqualTo(expectedResponse);

        verify(userRepository).existsByUsername("user1");
        verify(userRepository).existsByEmail("user1@test.com");
        verify(userMapper).toEntity(request, null);
        verify(userRepository).save(user);
        verify(userStatusRepository).save(userStatus);
        verify(userMapper).toResponse(savedUser, userStatus);
        verify(binaryContentService, never()).createBinaryContent(any());
    }

    @Test
    @DisplayName("사용자 생성 성공 - 프로필 있음")
    void create_success_withProfile() {
        UserCreateRequest request = new UserCreateRequest(
                "user1",
                "user1@test.com",
                "password"
        );

        BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest(
                "image".getBytes(),
                "profile.png",
                "image/png"
        );

        BinaryContent profile = BinaryContent.builder()
                .id(UUID.randomUUID())
                .fileName("profile.png")
                .size(5L)
                .contentType("image/png")
                .build();

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .profile(profile)
                .build();

        User savedUser = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .profile(profile)
                .build();

        UserStatus userStatus = UserStatus.builder()
                .user(savedUser)
                .lastActiveAt(Instant.now())
                .build();

        UserResponse expectedResponse = new UserResponse(
                userId,
                "user1",
                "user1@test.com",
                null,
                true
        );

        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@test.com")).thenReturn(false);
        when(binaryContentService.createBinaryContent(profileRequest)).thenReturn(profile);
        when(userMapper.toEntity(request, profile)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userStatusMapper.toEntity(any(User.class), any(Instant.class))).thenReturn(userStatus);
        when(userStatusRepository.save(userStatus)).thenReturn(userStatus);
        when(userMapper.toResponse(savedUser, userStatus)).thenReturn(expectedResponse);

        UserResponse result = userService.create(request, Optional.of(profileRequest));

        assertThat(result).isEqualTo(expectedResponse);

        verify(binaryContentService).createBinaryContent(profileRequest);
        verify(userMapper).toEntity(request, profile);
        verify(userRepository).save(user);
        verify(userStatusRepository).save(userStatus);
        verify(userMapper).toResponse(savedUser, userStatus);
    }

    @Test
    @DisplayName("사용자 생성 실패 - username 중복")
    void create_fail_duplicateUsername() {
        UserCreateRequest request = new UserCreateRequest(
                "user1",
                "user1@test.com",
                "password"
        );

        when(userRepository.existsByUsername("user1")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).existsByUsername("user1");
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 생성 실패 - email 중복")
    void create_fail_duplicateEmail() {
        UserCreateRequest request = new UserCreateRequest(
                "user1",
                "user1@test.com",
                "password"
        );

        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).existsByUsername("user1");
        verify(userRepository).existsByEmail("user1@test.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 수정 성공")
    void update_success() {
        UUID userId = UUID.randomUUID();

        UserUpdateRequest request = new UserUpdateRequest(
                "newUser",
                "new@test.com",
                "newPassword"
        );

        User user = User.builder()
                .id(userId)
                .username("oldUser")
                .email("old@test.com")
                .password("oldPassword")
                .build();

        UserResponse expectedResponse = new UserResponse(
                userId,
                "newUser",
                "new@test.com",
                null,
                null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponse result = userService.update(userId, request, Optional.empty());

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(user.getUsername()).isEqualTo("newUser");
        assertThat(user.getEmail()).isEqualTo("new@test.com");
        assertThat(user.getPassword()).isEqualTo("newPassword");

        verify(userRepository).findById(userId);
        verify(userRepository).existsByUsername("newUser");
        verify(userRepository).existsByEmail("new@test.com");
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("사용자 수정 실패 - 사용자 없음")
    void update_fail_userNotFound() {
        UUID userId = UUID.randomUUID();

        UserUpdateRequest request = new UserUpdateRequest(
                "newUser",
                "new@test.com",
                "newPassword"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(userId);
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    @DisplayName("사용자 수정 실패 - email 중복")
    void update_fail_duplicateEmail() {
        UUID userId = UUID.randomUUID();

        UserUpdateRequest request = new UserUpdateRequest(
                null,
                "duplicate@test.com",
                null
        );

        User user = User.builder()
                .id(userId)
                .username("user1")
                .email("old@test.com")
                .password("password")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("duplicate@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("duplicate@test.com");
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    @DisplayName("사용자 삭제 성공 - 프로필 없음")
    void delete_success_withoutProfile() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        UserStatus userStatus = UserStatus.builder()
                .id(UUID.randomUUID())
                .user(user)
                .lastActiveAt(Instant.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userStatusRepository.findByUser_Id(userId)).thenReturn(Optional.of(userStatus));

        userService.delete(userId);

        verify(userRepository).findById(userId);
        verify(userStatusRepository).findByUser_Id(userId);
        verify(userStatusRepository).delete(userStatus);
        verify(userRepository).delete(user);
        verify(binaryContentService, never()).delete(any());
    }

    @Test
    @DisplayName("사용자 삭제 실패 - 사용자 상태 없음")
    void delete_fail_userStatusNotFound() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userStatusRepository.findByUser_Id(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(UserStatusNotFoundException.class);

        verify(userRepository).findById(userId);
        verify(userStatusRepository).findByUser_Id(userId);
        verify(userRepository, never()).delete(any());
    }
}