package com.bigcrab.spring.cache;

/**
 * Created by luantao on 2017/3/6.
 */
public class User {

    private long id;

    private String name;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
