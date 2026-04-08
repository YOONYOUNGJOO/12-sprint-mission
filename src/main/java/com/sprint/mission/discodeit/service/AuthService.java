package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.dto.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;

public interface AuthService {
    User login(AuthLoginRequest dto);

}
