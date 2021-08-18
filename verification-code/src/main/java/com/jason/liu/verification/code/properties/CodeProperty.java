package com.jason.liu.verification.code.properties;

import com.jason.liu.verification.code.constants.CodeRuleType;
import com.jason.liu.verification.code.constants.ImageType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:验证码池配置
 */
@Data
@ConfigurationProperties(prefix = "jason.tools.verification.code")
public class CodeProperty {

    /**
     * 二维码图片类型
     */
    private ImageType imageType = ImageType.JPEG;

    /**
     * 规则类型，默认随机字符
     */
    private CodeRuleType ruleType = CodeRuleType.RANDOM_CHAR;

    /**
     * 随机字符串时表示，验证码字符长度
     */
    private Integer randomCharLength = 4;

    /**
     * 四则运算时表示取值范围的最大值
     */
    private Integer arithmeticRange = 100;

    /**
     * 图片尺寸配置
     * key: rule$imageType
     */
    private Map<ImageType, ImageSize> imageSizes = new HashMap<>();
    /**
     * 失效时间，单位s
     */
    private Long expire = 300L;
    /**
     * 缓存池
     */
    private Pool pool = new Pool();

    /**
     * 验证码图片尺寸
     */
    @Data
    public static class ImageSize implements Serializable {
        /**
         * 验证码图片宽度
         */
        private Integer width;
        /**
         * 验证码高度
         */
        private Integer height;

    }

    @Data
    public static class Pool implements Serializable {
        /**
         * 是否启用缓冲池
         */
        private Boolean enabled = true;
        /**
         * 最大数量
         */
        private Integer maxSize = 1000;
    }
}
