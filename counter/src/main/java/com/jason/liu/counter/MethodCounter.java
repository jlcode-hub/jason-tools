package com.jason.liu.counter;

import com.jason.liu.counter.utils.ValueExpression;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

/**
 * @author: meng.liu
 * @date: 2021/1/18
 * TODO:
 */
@Data
@Accessors(chain = true)
public class MethodCounter {

    private Count count;

    private Class targetClass;

    private Method method;

    private ICountRule rule;

    private ICounter counter;

    private ValueExpression valueExpression;
}
