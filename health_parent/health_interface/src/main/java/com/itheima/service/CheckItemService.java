package com.itheima.service;

import com.itheima.domain.CheckItem;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.HealthException;

import java.util.List;

/**
 * @Author：hushiqi
 * @Date：2020/9/18 20:17
 */
public interface CheckItemService {
    List<CheckItem> findAll();

    void add(CheckItem checkItem);

    PageResult<CheckItem> findPage(QueryPageBean queryPageBean);

    CheckItem findById(Integer id);

    void update(CheckItem checkItem);

    void deleteById(Integer id)throws HealthException;
}
