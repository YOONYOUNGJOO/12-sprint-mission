package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(UserCreateRequest dto);
    UserResponse findById(UUID userId);
    List<UserResponse> findAll();
    UserResponse update(UserUpdateRequest dto);
    void delete(UUID userId);
}
