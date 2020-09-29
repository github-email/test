package com.itheima.service;

import com.itheima.domain.Order;
import com.itheima.exception.HealthException;

import java.util.Map;

/**
 * @Author：hushiqi
 * @Date：2020/9/28 20:52
 */
public interface OrderService {
    Order submit(Map<String, String> orderInfo)throws HealthException;

    Map<String,String> findById(Integer id);
}
