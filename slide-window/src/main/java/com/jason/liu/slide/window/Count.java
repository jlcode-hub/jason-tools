package com.jason.liu.slide.window;

import lombok.Data;

/**
 * @author: meng.liu
 * @date: 2021/3/23
 * TODO:
 */
@Data
public class Count {

    private long timestamp;

    private long count;

    public Count(long timestamp, long count) {
        this.timestamp = timestamp;
        this.count = count;
    }
}
