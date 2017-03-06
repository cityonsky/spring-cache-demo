package com.bigcrab.spring.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by luantao on 2017/3/6.
 */
@Component
public class UserClient {

    @Autowired
    private UserService userService;


    public void run() {
        addUsers();
        getUsers();
    }

    private void addUsers() {
        for (long i = 0; i < 100; ++i) {
            String name = String.format("user_%d", i);
            userService.addUser(new User(i, name));
        }
    }

    private void getUsers() {
        getUser(1L);
        getUser(20L);
        getUser(101L);
        getUser(1L);
        getUser(20L);
        getUser(101L);
    }

    private void getUser(long id) {
        System.out.println("=== start getting user who's id is " + id + " ===");
        User user = userService.getUser(id);
        String log = String.format("user id = %d, user name = %s", id, user != null ? user.getName() : null);
        System.out.println(log);
    }

}
