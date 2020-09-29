package com.itheima.dao;

import com.itheima.domain.Order;

import java.util.List;
import java.util.Map;

/**
 * @Author：hushiqi
 * @Date：2020/9/28 21:18
 */
public interface OrderDao {
    List<Order> findByCondition(Order order);

    Map<String,String> findById(Integer id);

    void add(Order order);
}
