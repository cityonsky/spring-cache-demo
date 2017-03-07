## 概览
缓存是让数据更接近于使用者；工作机制是先从缓存中读取数据，如果没有再从慢速设备上读取实际数据（数据也会存入缓存）；缓存的是那些经常读取且不经常修改的数据/那些昂贵（CPU/IO）的且对于相同的请求有相同的计算结果的数据。

先介绍几个重要概念：
- 缓存命中率： 从缓存中读取次数 / 总读取次数，这是衡量缓存效率的核心指标
- 缓存清理策略： FIFO、LRU、LFU
- TTL：存活期，即从缓存中创建时间点开始直到它到期的一个时间段
- TTI：空闲期，即一个数据多久没被访问将从缓存中移除的时间。

自 Spring 3.1 起，提供了 Cache 抽象和基于注解的 Cache 支持，带来如下好处：
- 提供基本的 Cache 抽象，方便切换各种底层 Cache；
- 通过注解 Cache 可以实现类似于事务一样，缓存逻辑透明的应用到我们的业务代码上，且只需要更少的代码就可以完成；
- 提供事务回滚时也自动回滚缓存；
- 支持比较复杂的缓存逻辑。


## 快速上手
[github 代码地址](https://github.com/cityonsky/spring-cache-demo)

一、Maven 配置
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
          http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.bigcrab.spring</groupId>
    <artifactId>cache-demo</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <springframework.version>4.3.6.RELEASE</springframework.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
            <version>${springframework.version}</version>
        </dependency>
    </dependencies>

</project>
```

二、Spring 的 applicationContext.xml 配置
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
       http://www.springframework.org/schema/beans/spring-beans.xsd 
       http://www.springframework.org/schema/context 
       http://www.springframework.org/schema/context/spring-context.xsd">

       <context:component-scan base-package="com.bigcrab.spring.cache"/>

</beans>
```

三、最简单的缓存代码配置
```
package com.bigcrab.spring.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by luantao on 2017/3/6.
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
public class AppConfig implements CachingConfigurer {

    private CacheManager cacheManager;

    @PostConstruct
    public void init() {
        cacheManager = new ConcurrentMapCacheManager();
    }

    @Override
    public CacheManager cacheManager() {
        return null;
    }

    @Override
    public CacheResolver cacheResolver() {
        return context -> context.getOperation().getCacheNames()
                .stream()
                .map(cacheManager::getCache)
                .collect(Collectors.toList());
    }

    @Override
    public KeyGenerator keyGenerator() {
        return null;
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return null;
    }

}
```

四、User 数据定义
```
package com.bigcrab.spring.cache;

/**
 * Created by luantao on 2017/3/6.
 */
public class User {

    private Long id;

    private String name;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

五、User 管理服务实现
```
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

```

六、测试逻辑
```
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
```

七、主函数
```
package com.bigcrab.spring.cache;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by luantao on 2017/3/6.
 */
public class Main {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserClient client = ctx.getBean(UserClient.class);
        client.run();
    }

}

```

八、测试结果
```
=== start getting user who's id is 1 ===
get user in user service
user id = 1, user name = user_1
=== start getting user who's id is 20 ===
get user in user service
user id = 20, user name = user_20
=== start getting user who's id is 101 ===
get user in user service
user id = 101, user name = null
=== start getting user who's id is 1 ===
user id = 1, user name = user_1
=== start getting user who's id is 20 ===
user id = 20, user name = user_20
=== start getting user who's id is 101 ===
user id = 101, user name = null
```

## 条件缓存
Srping Cache 框架允许通过 condition 或者 unless 字段增加一些缓存控制策略。

- @Cacheable 将在执行方法之前(#result还拿不到返回值)判断 condition，如果返回 true，则查缓存：
```
@Cacheable(value = "user", key = "#id", condition = "#id lt 10")  
public User conditionFindById(final Long id)  
```

- @CachePut 将在执行完方法后（#result就能拿到返回值了）判断 condition，如果返回 true，则放入缓存：
```
@CachePut(value = "user", key = "#id", condition = "#result.name ne 'foo'")  
public User conditionSave(final User user)   
```

- @CachePut 将在执行完方法后（#result就能拿到返回值了）判断 unless，如果返回 false，则放入缓存:
```
@CachePut(value = "user", key = "#user.id", unless = "#result.name eq 'foo'")  
public User conditionSave(final User user)   
```

- @CacheEvict， beforeInvocation=false表示在方法执行之后调用（#result能拿到返回值了）；且判断condition，如果返回true，则移除缓存：
```
@CacheEvict(value = "user", key = "#user.id", beforeInvocation = false, condition = "#result.name ne 'foo'")  
public User conditionDelete(final User user)   
```

## 组合注解
可以使用 @Caching 把多个缓存注解组合在一起，如下：
```
@Caching(
            put = {
                    @CachePut(value = "user", key = "#user.id"),
                    @CachePut(value = "user_name", key = "#user.name")
            }
    )
    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }
```

也可以自己定义一个注解，这样使用的地方就会简洁很多：
```
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
```

addUser 就改为：
```
@CacheUser
    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }
```

