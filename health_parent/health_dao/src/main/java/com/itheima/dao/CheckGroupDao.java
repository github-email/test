package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.domain.CheckGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author：hushiqi
 * @Date：2020/9/20 20:32
 */
public interface CheckGroupDao {
    Page<CheckGroup> findPage(String queryString);

    void add(CheckGroup checkGroup);

    void addCheckGroupCheckItem(@Param("groupId")Integer groupId, @Param("checkitemId")Integer checkitemId);

    CheckGroup findById(Integer id);

    void update(CheckGroup checkGroup);

    List<CheckGroup> findCheckItemIdsToCheckGroupId(Integer id);

    void deleteCheckGroupToCheckItem(Integer id);

    int findSetmealCountByCheckGroupId(Integer id);

    void deleteById(Integer id);

    List<CheckGroup> findAll();
}
