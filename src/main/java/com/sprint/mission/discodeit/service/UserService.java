package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User save(User user);
    User findById(UUID id);
    List<User> findAll();
    User updateUsername(UUID id , String name);
    User updateEmail(UUID id, String email);
    User updateNickname(UUID id, String nickname);
    User updatePassword(UUID id, String password);
    User updatePhoneNumber(UUID id, String phoneNum);
    User deleteById(UUID id);
    User softDeleteById(UUID id);




}