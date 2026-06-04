package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ChannelMapper channelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private ReadStatusMapper readStatusMapper;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicChannelService channelService;

    @Test
    @DisplayName("PUBLIC 채널 생성 성공")
    void createPublicChannel_success() {
        CreatePublicChannelRequest request = new CreatePublicChannelRequest(
                "public-channel",
                "public-description"
        );

        UUID channelId = UUID.randomUUID();

        Channel savedChannel = Channel.builder()
                .id(channelId)
                .type(ChannelType.PUBLIC)
                .name("public-channel")
                .description("public-description")
                .build();

        ChannelResponse expectedResponse = new ChannelResponse(
                channelId,
                ChannelType.PUBLIC,
                "public-channel",
                "public-description",
                List.of(),
                null
        );

        when(channelRepository.save(any(Channel.class))).thenReturn(savedChannel);
        when(channelMapper.toResponse(savedChannel, null, List.of())).thenReturn(expectedResponse);

        ChannelResponse result = channelService.createPublicChannel(request);

        assertThat(result).isEqualTo(expectedResponse);

        verify(channelRepository).save(any(Channel.class));
        verify(channelMapper).toResponse(savedChannel, null, List.of());
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 성공")
    void createPrivateChannel_success() {
        UUID channelId = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        CreatePrivateChannelRequest request = new CreatePrivateChannelRequest(
                List.of(userId1, userId2)
        );

        Channel savedChannel = Channel.builder()
                .id(channelId)
                .type(ChannelType.PRIVATE)
                .build();

        User user1 = User.builder()
                .id(userId1)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        User user2 = User.builder()
                .id(userId2)
                .username("user2")
                .email("user2@test.com")
                .password("password")
                .build();

        ReadStatus readStatus1 = ReadStatus.builder()
                .user(user1)
                .channel(savedChannel)
                .lastReadAt(Instant.now())
                .build();

        ReadStatus readStatus2 = ReadStatus.builder()
                .user(user2)
                .channel(savedChannel)
                .lastReadAt(Instant.now())
                .build();

        UserResponse userResponse1 = new UserResponse(
                userId1,
                "user1",
                "user1@test.com",
                null,
                null
        );

        UserResponse userResponse2 = new UserResponse(
                userId2,
                "user2",
                "user2@test.com",
                null,
                null
        );

        ChannelResponse expectedResponse = new ChannelResponse(
                channelId,
                ChannelType.PRIVATE,
                null,
                null,
                List.of(userResponse1, userResponse2),
                null
        );

        when(channelRepository.save(any(Channel.class))).thenReturn(savedChannel);
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));
        when(readStatusMapper.toEntity(any(User.class), any(Channel.class), any(Instant.class)))
                .thenReturn(readStatus1, readStatus2);
        when(userMapper.toResponse(user1)).thenReturn(userResponse1);
        when(userMapper.toResponse(user2)).thenReturn(userResponse2);
        when(channelMapper.toResponse(
                savedChannel,
                null,
                List.of(userResponse1, userResponse2)
        )).thenReturn(expectedResponse);

        ChannelResponse result = channelService.createPrivateChannel(request);

        assertThat(result).isEqualTo(expectedResponse);

        verify(channelRepository).save(any(Channel.class));
        verify(userRepository).findById(userId1);
        verify(userRepository).findById(userId2);
        verify(readStatusRepository).save(readStatus1);
        verify(readStatusRepository).save(readStatus2);
        verify(userMapper).toResponse(user1);
        verify(userMapper).toResponse(user2);
        verify(channelMapper).toResponse(savedChannel, null, List.of(userResponse1, userResponse2));
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 실패 - 참여자 없음")
    void createPrivateChannel_fail_userNotFound() {
        UUID userId = UUID.randomUUID();

        CreatePrivateChannelRequest request = new CreatePrivateChannelRequest(
                List.of(userId)
        );

        Channel savedChannel = Channel.builder()
                .id(UUID.randomUUID())
                .type(ChannelType.PRIVATE)
                .build();

        when(channelRepository.save(any(Channel.class))).thenReturn(savedChannel);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> channelService.createPrivateChannel(request))
                .isInstanceOf(UserNotFoundException.class);

        verify(channelRepository).save(any(Channel.class));
        verify(userRepository).findById(userId);
        verify(readStatusRepository, never()).save(any());
        verify(channelMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    @DisplayName("채널 수정 성공 - PUBLIC 채널")
    void update_success_publicChannel() {
        UUID channelId = UUID.randomUUID();

        ChannelUpdateRequest request = new ChannelUpdateRequest(
                "new-name",
                "new-description"
        );

        Channel channel = Channel.builder()
                .id(channelId)
                .type(ChannelType.PUBLIC)
                .name("old-name")
                .description("old-description")
                .build();

        ChannelResponse expectedResponse = new ChannelResponse(
                channelId,
                ChannelType.PUBLIC,
                "new-name",
                "new-description",
                List.of(),
                null
        );

        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(messageRepository.findFirstByChannel_IdOrderByCreatedAtDesc(channelId))
                .thenReturn(Optional.empty());
        when(channelMapper.toResponse(channel, null, List.of())).thenReturn(expectedResponse);

        ChannelResponse result = channelService.update(channelId, request);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(channel.getName()).isEqualTo("new-name");
        assertThat(channel.getDescription()).isEqualTo("new-description");

        verify(channelRepository).findById(channelId);
        verify(messageRepository).findFirstByChannel_IdOrderByCreatedAtDesc(channelId);
        verify(channelMapper).toResponse(channel, null, List.of());
    }

    @Test
    @DisplayName("채널 수정 실패 - 채널 없음")
    void update_fail_channelNotFound() {
        UUID channelId = UUID.randomUUID();

        ChannelUpdateRequest request = new ChannelUpdateRequest(
                "new-name",
                "new-description"
        );

        when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> channelService.update(channelId, request))
                .isInstanceOf(ChannelNotFoundException.class);

        verify(channelRepository).findById(channelId);
        verify(channelMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    @DisplayName("채널 수정 실패 - PRIVATE 채널")
    void update_fail_privateChannel() {
        UUID channelId = UUID.randomUUID();

        ChannelUpdateRequest request = new ChannelUpdateRequest(
                "new-name",
                "new-description"
        );

        Channel channel = Channel.builder()
                .id(channelId)
                .type(ChannelType.PRIVATE)
                .build();

        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));

        assertThatThrownBy(() -> channelService.update(channelId, request))
                .isInstanceOf(PrivateChannelUpdateNotAllowedException.class);

        verify(channelRepository).findById(channelId);
        verify(channelMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void delete_success() {
        UUID channelId = UUID.randomUUID();
        UUID messageId1 = UUID.randomUUID();
        UUID messageId2 = UUID.randomUUID();

        Channel channel = Channel.builder()
                .id(channelId)
                .type(ChannelType.PUBLIC)
                .name("channel")
                .build();

        Message message1 = Message.builder()
                .id(messageId1)
                .channel(channel)
                .content("message1")
                .build();

        Message message2 = Message.builder()
                .id(messageId2)
                .channel(channel)
                .content("message2")
                .build();

        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(messageRepository.findAllByChannel_Id(channelId)).thenReturn(List.of(message1, message2));

        channelService.delete(channelId);

        verify(channelRepository).findById(channelId);
        verify(messageRepository).findAllByChannel_Id(channelId);
        verify(messageService).delete(messageId1);
        verify(messageService).delete(messageId2);
        verify(channelRepository).delete(channel);
    }

    @Test
    @DisplayName("채널 삭제 실패 - 채널 없음")
    void delete_fail_channelNotFound() {
        UUID channelId = UUID.randomUUID();

        when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> channelService.delete(channelId))
                .isInstanceOf(ChannelNotFoundException.class);

        verify(channelRepository).findById(channelId);
        verify(messageRepository, never()).findAllByChannel_Id(any());
        verify(channelRepository, never()).delete(any());
    }

    @Test
    @DisplayName("사용자 ID로 채널 목록 조회 성공")
    void findAllByUserId_success() {
        UUID userId = UUID.randomUUID();
        UUID publicChannelId = UUID.randomUUID();
        UUID privateChannelId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        Channel publicChannel = Channel.builder()
                .id(publicChannelId)
                .type(ChannelType.PUBLIC)
                .name("public-channel")
                .description("public-description")
                .build();

        Channel privateChannel = Channel.builder()
                .id(privateChannelId)
                .type(ChannelType.PRIVATE)
                .build();

        ReadStatus readStatus = ReadStatus.builder()
                .user(user)
                .channel(privateChannel)
                .lastReadAt(Instant.now())
                .build();

        UserResponse userResponse = new UserResponse(
                userId,
                "user1",
                "user1@test.com",
                null,
                null
        );

        ChannelResponse publicResponse = new ChannelResponse(
                publicChannelId,
                ChannelType.PUBLIC,
                "public-channel",
                "public-description",
                List.of(),
                null
        );

        ChannelResponse privateResponse = new ChannelResponse(
                privateChannelId,
                ChannelType.PRIVATE,
                null,
                null,
                List.of(userResponse),
                null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(readStatusRepository.findAllByUser_Id(userId))
                .thenReturn(List.of(readStatus));
        when(channelRepository.findAllByTypeOrIdIn(
                ChannelType.PUBLIC,
                List.of(privateChannelId)
        )).thenReturn(List.of(publicChannel, privateChannel));

        when(messageRepository.findFirstByChannel_IdOrderByCreatedAtDesc(publicChannelId))
                .thenReturn(Optional.empty());
        when(messageRepository.findFirstByChannel_IdOrderByCreatedAtDesc(privateChannelId))
                .thenReturn(Optional.empty());

        when(readStatusRepository.findAllByChannelIdWithUser(privateChannelId))
                .thenReturn(List.of(readStatus));

        when(userMapper.toResponse(user)).thenReturn(userResponse);
        when(channelMapper.toResponse(publicChannel, null, List.of()))
                .thenReturn(publicResponse);
        when(channelMapper.toResponse(privateChannel, null, List.of(userResponse)))
                .thenReturn(privateResponse);

        List<ChannelResponse> result = channelService.findAllByUserId(userId);

        assertThat(result).containsExactly(publicResponse, privateResponse);

        verify(userRepository).findById(userId);
        verify(readStatusRepository).findAllByUser_Id(userId);
        verify(channelRepository).findAllByTypeOrIdIn(
                ChannelType.PUBLIC,
                List.of(privateChannelId)
        );
        verify(messageRepository).findFirstByChannel_IdOrderByCreatedAtDesc(publicChannelId);
        verify(messageRepository).findFirstByChannel_IdOrderByCreatedAtDesc(privateChannelId);
        verify(readStatusRepository).findAllByChannelIdWithUser(privateChannelId);
        verify(userMapper).toResponse(user);
        verify(channelMapper).toResponse(publicChannel, null, List.of());
        verify(channelMapper).toResponse(privateChannel, null, List.of(userResponse));
    }

    @Test
    @DisplayName("사용자 ID로 채널 목록 조회 실패 - 사용자 없음")
    void findAllByUserId_fail_userNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> channelService.findAllByUserId(userId))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(userId);
        verify(readStatusRepository, never()).findAllByUser_Id(any());
        verify(channelRepository, never()).findAllByTypeOrIdIn(any(), any());
    }
}