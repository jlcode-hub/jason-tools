package com.jason.liu.time.consume;

import lombok.Data;

/**
 * @author: meng.liu
 * @date: 2021/1/8
 * TODO:
 */
@Data
public class MethodInfo {

    private String key;

    private String className;

    private String methodName;

    private TimeConsume timeConsume;

    private int classLen;

    private int methodLen;

    private String title;
}
