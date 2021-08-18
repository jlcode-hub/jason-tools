package com.jason.liu.nacos.refresh.utils;

import com.alibaba.nacos.api.config.ConfigType;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: meng.liu
 * @date: 2020/8/5
 * TODO:
 */
public class ConfigTypeUtils {

    /**
     * Type是否合法
     *
     * @param type
     * @return
     */
    public static boolean isConfigType(String type) {
        if (StringUtils.isBlank(type)) {
            return false;
        }
        for (ConfigType value : ConfigType.values()) {
            if (value.name().equals(type) || value.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

}
