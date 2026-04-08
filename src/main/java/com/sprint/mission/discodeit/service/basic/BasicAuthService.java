package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.dto.AuthLoginRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    public BasicAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(AuthLoginRequest dto) {
        Optional<User> user = userRepository.findAll().stream().
                filter(u -> u.getUsername().equals(dto.username()) &&
                                u.getPassword().equals(dto.password())).findFirst();
        if (user.isEmpty()) {
            throw new NoSuchElementException("No user found matching username and password");
        }
        return user.get();
    }
}