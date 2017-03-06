package com.bigcrab.spring.cache;

/**
 * Created by luantao on 2017/3/6.
 */
public class TrackingLogic {

    public static final long INVALID_ID = -1L;

    private long actionUserId;

    public void logGetUserAction(long id) {
        actionUserId = id;
    }

    public void clear() {
        actionUserId = INVALID_ID;
    }

    public long getActionUserId() {
        return actionUserId;
    }
}
