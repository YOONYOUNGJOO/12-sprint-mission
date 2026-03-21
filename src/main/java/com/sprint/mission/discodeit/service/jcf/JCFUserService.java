package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;
    public JCFUserService() {
        data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        data.put(user.getUserId(),user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        if(data.get(id) == null){
            throw new IllegalArgumentException("존재하지 않는 사용자 입니다");
        }
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
       List<User> users = new ArrayList<>(data.values());
       users.sort((u1, u2) -> Long.compare(u1.getCreatedAt(), u2.getCreatedAt()));
       return users;


    }

    @Override
    public User updateUsername(UUID id, String username) {
        User user = findById(id);
        user.updateUsername(username);
        return user;
    }

    @Override
    public User updateEmail(UUID id, String email) {
        User user = findById(id);
        user.updateEmail(email);
        return user;
    }

    @Override
    public User updatePassword(UUID id, String password) {
        User user = findById(id);
        user.updatePassword(password);
        return user;
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

}