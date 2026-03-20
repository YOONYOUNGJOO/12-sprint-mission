package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data;
    public JCFUserService() {
        data = new ArrayList<>();
    }

    @Override
    public User save(User user) {
        data.add(user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.stream().filter(user -> user.getUserId().equals(id)).findFirst().orElseThrow();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public User updateUsername(UUID id, String username) {
        User userId = findById(id);
        userId.updateUsername(username);
        return userId;
    }

    @Override
    public User updateEmail(UUID id, String email) {
        User userId = findById(id);
        userId.updateEmail(email);
        return userId;
    }

    @Override
    public User updateNickname(UUID id, String nickname) {
        User userId = findById(id);
        userId.updateNickname(nickname);
        return userId;
    }

    @Override
    public User updatePassword(UUID id, String password) {
        User userId = findById(id);
        userId.updatePassword(password);
        return userId;
    }

    @Override
    public User updatePhoneNumber(UUID id, String phoneNumber) {
        User userId = findById(id);
        userId.updatePhoneNumber(phoneNumber);
        return userId;
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(findById(id));
    }

    @Override
    public User softDeleteById(UUID id) {
        User userId = findById(id);
        userId.updateActive(false);
        return userId;

    }
}