package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.domain.CheckItem;

import java.util.List;

/**
 * @Author：hushiqi
 * @Date：2020/9/18 19:52
 */
public interface CheckItemDao {
    List<CheckItem> findAll();

    /**
     * 添加
     * @param checkItem
     */
    void add(CheckItem checkItem);

    Page<CheckItem> findPage(String queryString);

    CheckItem findById(Integer id);

    void update(CheckItem checkItem);

    void deleteById(Integer id);

    int findCountByCheckItemId(Integer id);
}
