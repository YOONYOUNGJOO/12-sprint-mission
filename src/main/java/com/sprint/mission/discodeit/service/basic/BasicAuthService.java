package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import com.sprint.mission.discodeit.dto.Auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse login(LoginRequest request) {
        User foundUser = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(request.username())
                        && u.getPassword().equals(request.password()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Invalid username or password"));

        UserStatus userStatus = userStatusRepository.findByUser_Id(foundUser.getId())
                .orElseThrow(() -> new NoSuchElementException("User status not found"));

        return userMapper.toResponse(foundUser, userStatus);
    }
}