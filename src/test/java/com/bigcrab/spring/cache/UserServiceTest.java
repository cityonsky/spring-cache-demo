package com.bigcrab.spring.cache;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.Test
    public void testAddUser() throws Exception {
        addUsers();

        TrackingLogic trackingLogic = new TrackingLogic();
        userService.setTrackingLogic(trackingLogic);

        // 第一次获取用户，从 userService 中生成
        trackingLogic.clear();
        User user = userService.getUser(1L);
        Assert.assertEquals("User_1", user.getName());
        Assert.assertEquals(1L, trackingLogic.getActionUserId());

        // 第二次获取用户，从缓存中获取
        trackingLogic.clear();
        user = userService.getUser(1L);
        Assert.assertEquals("User_1", user.getName());
        Assert.assertEquals(TrackingLogic.INVALID_ID, trackingLogic.getActionUserId());

        // 删除用户，同时清理缓存，再次获取用户，从 userService 中生成
        trackingLogic.clear();
        userService.deleteUser(1L);
        user = userService.getUser(1L);
        Assert.assertNull(user);
        Assert.assertEquals(1L, trackingLogic.getActionUserId());

        // 第二次获取用户，从缓存中获取
        trackingLogic.clear();
        user = userService.getUser(1L);
        Assert.assertNull(user);
        Assert.assertEquals(TrackingLogic.INVALID_ID, trackingLogic.getActionUserId());
    }

    private void addUsers() {
        for (long i = 0; i < 100; ++i) {
            String name = String.format("User_%d", i);
            userService.addUser(new User(i, name));
        }
    }

}