package com.itheima.service;

import com.itheima.domain.Setmeal;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.HealthException;

import java.util.List;

/**
 * @Author：hushiqi
 * @Date：2020/9/22 11:38
 */
public interface SetmealService {
    PageResult<Setmeal> findPage(QueryPageBean queryPageBean);

    Integer add(Setmeal setmeal, Integer[] checkgroupIds);

    Setmeal findById(Integer id);

    List<Setmeal> findByCheckGroupIdToSetmealId(Integer id);

    void update(Setmeal setmeal, Integer[] checkGroupIds);

    void deleteById(Integer id) throws HealthException;

    List<String> findImgs();

    List<Setmeal> findAll();

    Setmeal findDetailById(Integer id);
}
