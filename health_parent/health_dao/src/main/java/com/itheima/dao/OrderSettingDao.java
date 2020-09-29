package com.itheima.dao;


import com.itheima.domain.OrderSetting;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author：hushiqi
 * @Date：2020/9/24 10:05
 */
public interface OrderSettingDao {
    /**
     * 通过日期来查询预约设置
     * @param orderDate
     * @return
     */
    OrderSetting findByOrderData(Date orderDate);

    /**
     * 更新预约设置
     * @param orderSetting
     */
    void updateNumber(OrderSetting orderSetting);

    /**
     * 添加预约设置
     * @param orderSetting
     */
    void add(OrderSetting orderSetting);


    /**
     * 通过月份获取预约设置数据
     * @param starDate
     * @param endDate
     * @return
     */
    List<Map<String,Integer>> getOrderSettingByMonth(@Param("starDate") String starDate,@Param("endDate") String endDate);

    /**
     * 更新已预约人数
     */
    int editReservationsByOrderDate(OrderSetting orderSetting);
}
