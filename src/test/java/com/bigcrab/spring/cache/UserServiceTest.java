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
        userService.addUser(new User(1L, "User_1"));

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

    @org.junit.Test
    public void testModifyUser() throws Exception {
        userService.addUser(new User(1L, "User_1"));

        // 第一次获取用户，从 userService 中生成
        User user = userService.getUser(1L);
        Assert.assertEquals("User_1", user.getName());

        // 更改用户名字后，从缓存中也应该获取最新版本的数据
        User u = new User(1L, "foo");
        userService.modifyUser(u);
        user = userService.getUser(1L);
        Assert.assertEquals("foo", user.getName());
    }

    @org.junit.Test
    public void testCaching() throws Exception {
        userService.addUser2(new User(1L, "User_1"));

        User user = userService.getUser(1L);
        Assert.assertEquals("User_1", user.getName());
        Assert.assertEquals(1L, user.getId());

        user = userService.getUserByName("User_1");
        Assert.assertEquals("User_1", user.getName());
        Assert.assertEquals(1L, user.getId());
    }

    @org.junit.Test
    public void testCaching2() throws Exception {
        userService.addUser3(new User(1L, "User_1"));

        User user = userService.getUser(1L);
        Assert.assertEquals("User_1", user.getName());
        Assert.assertEquals(1L, user.getId());

        user = userService.getUserByName("User_1");
        Assert.assertEquals("User_1", user.getName());
        Assert.assertEquals(1L, user.getId());
    }

}