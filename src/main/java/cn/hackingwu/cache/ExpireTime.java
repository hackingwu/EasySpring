package cn.hackingwu.cache;

import java.util.concurrent.TimeUnit;

/**
 * @author hackingwu.
 * @since 2015/08/17
 */
public class ExpireTime {

    long time;
    TimeUnit timeUnit;

    public ExpireTime(long time, TimeUnit timeUnit) {
        this.time = time;
        this.timeUnit = timeUnit;
    }

    public long getTime() {
        return time;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

}
