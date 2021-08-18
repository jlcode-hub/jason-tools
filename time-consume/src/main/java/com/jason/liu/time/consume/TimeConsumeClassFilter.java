package com.jason.liu.time.consume;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.ClassFilter;

/**
 * @author: meng.liu
 * @date: 2021/1/7
 * TODO:
 */
@Slf4j
public class TimeConsumeClassFilter implements ClassFilter {

    private String[] basePackages;

    public TimeConsumeClassFilter(String[] basePackages) {
        this.basePackages = basePackages;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        boolean support = this.classMatch(clazz);
        if (log.isTraceEnabled()) {
            log.trace("class {} match result: {}", clazz, support);
        }
        return support;
    }

    /**
     * 接口父类注解支持
     *
     * @param clazz
     * @return
     */
    private boolean classMatch(Class<?> clazz) {
        if (checkByClassName(clazz)) {
            return true;
        }
        Class[] cs = clazz.getInterfaces();
        if (cs != null) {
            for (Class c : cs) {
                if (classMatch(c)) {
                    return true;
                }
            }
        }
        if (!clazz.isInterface()) {
            Class sp = clazz.getSuperclass();
            if (sp != null && classMatch(sp)) {
                return true;
            }
        }
        return false;
    }


    public boolean checkByClassName(Class clazz) {
        String name = clazz.getName();
        if (this.checkExcludeByName(name)) {
            return false;
        }
        return checkIncludeByName(name);
    }

    /**
     * 检测类是否在指定报名下
     *
     * @param name
     * @return
     */
    protected boolean checkIncludeByName(String name) {
        if (basePackages != null) {
            for (String p : basePackages) {
                if (name.startsWith(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 排除默认类
     *
     * @param name
     * @return
     */
    protected boolean checkExcludeByName(String name) {
        if (name.startsWith("java")) {
            return true;
        }
        if (name.startsWith("org.springframework")) {
            return true;
        }
        if (name.contains("$$EnhancerBySpringCGLIB$$")) {
            return true;
        }
        if (name.contains("$$FastClassBySpringCGLIB$$")) {
            return true;
        }
        return false;
    }

}
