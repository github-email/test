package com.itheima.service;

import com.itheima.domain.OrderSetting;
import com.itheima.exception.HealthException;

import java.util.List;
import java.util.Map;

/**
 * @Author：hushiqi
 * @Date：2020/9/24 10:03
 */
public interface OrderSettingService {
    void add(List<OrderSetting> orderList)throws HealthException;

    List<Map<String,Integer>> getOrderSettingByMonth(String month);

    void updateUsersData(OrderSetting orderSetting)throws HealthException;
}
