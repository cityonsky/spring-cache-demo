package com.bigcrab.spring.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by luantao on 2017/3/6.
 */
@Service
public class UserService {

    private Map<Long, User> users = new ConcurrentHashMap<>();

    private TrackingLogic trackingLogic = null;

    public void setTrackingLogic(TrackingLogic trackingLogic) {
        this.trackingLogic = trackingLogic;
    }

    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @CacheUser
    public User addUser2(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Caching(
            put = {
                    @CachePut(value = "user", key = "#user.id"),
                    @CachePut(value = "user_name", key = "#user.name")
            }
    )
    public User addUser3(User user) {
        users.put(user.getId(), user);
        return user;
    }

    // 应用到写数据的方法上，如新增/修改方法，调用方法时会自动把相应的数据放入缓存
    @CachePut(value = "user", key = "#user.id")
    public User modifyUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    // 获取 User 成功后，添加 User 到缓存系统中，key 为用户 id
    @Cacheable(value = "user", key = "#id")
    public User getUser(long id) {
        if (trackingLogic != null) {
            trackingLogic.logGetUserAction(id);
        }
        return users.get(id);
    }

    @Cacheable(value = "user_name", key = "#name")
    public User getUserByName(String name) {
        // 注释掉获取逻辑，以验证缓存的有效性
        //for (User user : users.values()) {
        //    if (user.getName().equals(name)) {
        //        return user;
        //    }
        //}

        return null;
    }

    // 删除用户后，从缓存中清理掉对应的数据
    @CacheEvict(value = "user", key = "#id")
    public void deleteUser(long id) {
        users.remove(id);
    }

}
