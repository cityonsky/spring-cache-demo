package com.bigcrab.spring.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by luantao on 2017/3/6.
 */
@Service
public class UserService {

    private Map<Long, User> users = new ConcurrentHashMap<>();

    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Cacheable(value = "user", key = "#id")
    public User getUser(Long id) {
        System.out.println("get user in user service");
        return users.get(id);
    }

}
