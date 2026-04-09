package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        AuthService authService = context.getBean(AuthService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
        UserStatusService userStatusService = context.getBean(UserStatusService.class);

        // 1. 유저 생성
        UserResponse user1 = userService.create(
                new UserCreateRequest("woody", "woody@test.com", "1234", null)
        );
        UserResponse user2 = userService.create(
                new UserCreateRequest("buzz", "buzz@test.com", "1234", null)
        );

        System.out.println("=== USER CREATE ===");
        System.out.println(user1);
        System.out.println(user2);

        // 2. 로그인
        System.out.println("=== AUTH LOGIN ===");
        System.out.println(authService.login(new AuthLoginRequest("woody", "1234")));

        // 3. public 채널 생성
        var publicChannel = channelService.createPublicChannel(
                new CreatePublicChannelRequest("공지", "공지 채널입니다.")
        );

        System.out.println("=== PUBLIC CHANNEL CREATE ===");
        System.out.println(publicChannel);

        // 4. private 채널 생성
        var privateChannel = channelService.createPrivateChannel(
                new CreatePrivateChannelRequest(List.of(user1.id(), user2.id()))
        );

        System.out.println("=== PRIVATE CHANNEL CREATE ===");
        System.out.println(privateChannel);

        // 5. 채널 조회
        System.out.println("=== CHANNEL FIND BY ID ===");
        System.out.println(channelService.findById(publicChannel.id()));
        System.out.println(channelService.findById(privateChannel.id()));

        // 6. 유저 기준 채널 목록 조회
        System.out.println("=== CHANNEL FIND ALL BY USER ID ===");
        System.out.println(channelService.findAllByUserId(user1.id()));

        // 7. public 채널 수정
        System.out.println("=== CHANNEL UPDATE ===");
        System.out.println(channelService.update(
                new ChannelUpdateRequest(
                        publicChannel.id(),
                        "공지사항",
                        "수정된 공지 채널입니다."
                )
        ));

        // 8. 메시지 생성
        MessageResponse message = messageService.create(
                new MessageCreateRequest(
                        "안녕하세요.",
                        publicChannel.id(),
                        user1.id(),
                        null
                )
        );

        System.out.println("=== MESSAGE CREATE ===");
        System.out.println(message);

        // 9. 채널별 메시지 목록 조회
        System.out.println("=== MESSAGE FIND ALL BY CHANNEL ID ===");
        System.out.println(messageService.findAllByChannelId(publicChannel.id()));

        // 10. 메시지 수정
        System.out.println("=== MESSAGE UPDATE ===");
        System.out.println(messageService.update(
                new MessageUpdateRequest(message.id(), "수정된 메시지입니다.")
        ));

        // 11. ReadStatus 목록 조회
        System.out.println("=== READ STATUS FIND ALL BY USER ID ===");
        System.out.println(readStatusService.findAllByUserId(user1.id()));

        // 12. UserStatus 전체 조회
        System.out.println("=== USER STATUS FIND ALL ===");
        System.out.println(userStatusService.findAll());

        // 13. UserStatus userId 기준 갱신
        System.out.println("=== USER STATUS UPDATE BY USER ID ===");
        System.out.println(userStatusService.updateByUserId(user1.id()));

        // 14. 유저 수정
        System.out.println("=== USER UPDATE ===");
        System.out.println(userService.update(
                new UserUpdateRequest(
                        user1.id(),
                        "woody-updated",
                        "woody-updated@test.com",
                        "5678",
                        null
                )
        ));

        // 15. 유저 조회
        System.out.println("=== USER FIND BY ID ===");
        System.out.println(userService.findById(user1.id()));

        // 16. 유저 전체 조회
        System.out.println("=== USER FIND ALL ===");
        System.out.println(userService.findAll());

        // 17. 메시지 삭제
        System.out.println("=== MESSAGE DELETE ===");
        messageService.delete(message.id());
        System.out.println("deleted message id = " + message.id());

        // 18. 채널 삭제
        System.out.println("=== CHANNEL DELETE ===");
        channelService.delete(publicChannel.id());
        System.out.println("deleted channel id = " + publicChannel.id());

        context.close();
    }
}