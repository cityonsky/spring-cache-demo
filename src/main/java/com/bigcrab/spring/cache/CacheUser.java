package com.bigcrab.spring.cache;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

/**
 * Created by luantao on 2017/3/7.
 */
@Caching(
        put = {
                @CachePut(value = "user", key = "#user.id"),
                @CachePut(value = "user_name", key = "#user.name")
        }
)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CacheUser {
}
