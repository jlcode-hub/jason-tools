package com.jason.liu.mybatis.plus.support;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页信息
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-08-18 10:04:57
 */
@Data
public class PageInfo implements Serializable {

    public static final int DEFAULT_PAGE = 1;

    public static final int DEFAULT_LIMIT = 10;

    /**
     * 当前页，缺省值：1
     */
    private Integer curPage = DEFAULT_PAGE;

    /**
     * 分页大小，缺省值：10
     */
    private Integer pageLimit = DEFAULT_LIMIT;

}
