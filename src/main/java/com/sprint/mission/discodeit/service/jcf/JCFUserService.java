package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class JCFUserService implements UserService {

    public Scanner sc = new Scanner(System.in);
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
        for(User user : data){
            if(user.getUserId().equals(id)) return user;
        }
//        data.stream().filter(idata -> idata.getId().equals(id));
        return null;
    }

    @Override
    public List<User> findAll() {
        return data;
    }


}