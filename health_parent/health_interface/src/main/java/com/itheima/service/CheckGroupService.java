package com.itheima.service;

import com.itheima.domain.CheckGroup;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.HealthException;

import java.util.List;

/**
 * @Author：hushiqi
 * @Date：2020/9/20 20:24
 */
public interface CheckGroupService {
    PageResult<CheckGroup> findPage(QueryPageBean queryPageBean);

    void add(CheckGroup checkGroup, Integer[] checkitemIds);

    CheckGroup findById(Integer id);

    List<CheckGroup> findCheckItemIdsToCheckGroupId(Integer id);

    void update(CheckGroup checkGroup, Integer[] checkItemsIds);

    void deleteById(Integer id)throws HealthException;

    List<CheckGroup> findAll();
}
