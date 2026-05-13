package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.Auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new NoSuchElementException(
                        "User with username " + request.username() + " not found"
                ));

        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("Wrong password");
        }

        UserStatus userStatus = userStatusRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NoSuchElementException(
                        "UserStatus with userId " + user.getId() + " not found"
                ));

        return userMapper.toResponse(user, userStatus);
    }
}