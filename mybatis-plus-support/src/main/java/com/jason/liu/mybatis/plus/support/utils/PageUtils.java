package com.jason.liu.mybatis.plus.support.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jason.liu.mybatis.plus.support.PageInfo;
import com.jason.liu.mybatis.plus.support.QueryResult;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页转换工具
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-14 14:10:35
 */
public class PageUtils {

    /**
     * 转分页实体
     *
     * @param pageInfo
     * @param <T>
     * @return
     */
    public static <T> IPage<T> toPage(PageInfo pageInfo) {
        return new Page<>(pageInfo.getCurPage(), pageInfo.getPageLimit());
    }

    /**
     * 转分页实体
     *
     * @param page
     * @param <R>
     * @return
     */
    public static <R> QueryResult<R> toResult(IPage<R> page) {
        QueryResult<R> queryResult = new QueryResult<>();
        queryResult.setList(page.getRecords());
        queryResult.setTotal(page.getTotal());
        return queryResult;
    }

    /**
     * 转分页实体
     *
     * @param page
     * @param <R>
     * @return
     */
    public static <T, R> QueryResult<R> toResult(IPage<T> page, Function<T, R> function) {
        QueryResult<R> queryResult = new QueryResult<>();
        List<T> tList = page.getRecords();
        if (CollectionUtils.isEmpty(tList)) {
            queryResult.setList(Collections.emptyList());
        } else {
            queryResult.setList(tList.stream().map(function).collect(Collectors.toList()));
        }
        queryResult.setTotal(page.getTotal());
        return queryResult;
    }

    /**
     * 转分页实体
     *
     * @param page
     * @param <R>
     * @return
     */
    public static <T, R> QueryResult<R> toResults(IPage<T> page, Function<List<T>, List<R>> function) {
        QueryResult<R> queryResult = new QueryResult<>();
        List<T> tList = page.getRecords();
        if (CollectionUtils.isEmpty(tList)) {
            queryResult.setList(Collections.emptyList());
        } else {
            queryResult.setList(function.apply(tList));
        }
        queryResult.setTotal(page.getTotal());
        return queryResult;
    }
}
