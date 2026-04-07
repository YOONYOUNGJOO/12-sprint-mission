package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        BinaryContentCreateRequest profile
) {
}
