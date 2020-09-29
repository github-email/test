package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.domain.OrderSetting;
import com.itheima.exception.HealthException;
import com.itheima.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author：hushiqi
 * @Date：2020/9/24 10:03
 */
@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Override
    @Transactional
    public void add(List<OrderSetting> orderList) throws HealthException {
        //遍历集合中的元素
        for (OrderSetting orderSetting : orderList) {
            DataUtils(orderSetting);
            /*//通过日期来查询预约设置信息，判断是否存在；意：日期里是否有时分秒，数据库里的日期是没有时分秒的
           OrderSetting os = orderSettingDao.findByOrderData(orderSetting.getOrderDate());
           //进行非空判断
           if (null != os){
               //不为空，判断已预约数是否大于要设置的最大预约数
               if (os.getReservations() > orderSetting.getNumber()){
                   //大于，就报异常提示
                   throw  new HealthException(orderSetting.getOrderDate()+"中的已预约数量不能大于可预约数量");
               }
               //不大于，就更新预约设置
               orderSettingDao.updateNumber(orderSetting);
           }else {
               //不存在,调用业务层，添加预约设置
               orderSettingDao.add(orderSetting);
           }*/
        }
    }

    /**
     * 通过月份获取预约设置的数据
     *
     * @param month
     * @return
     */
    @Override
    public List<Map<String, Integer>> getOrderSettingByMonth(String month) {
        //拼接月份的1到31号
        String starDate = month + "-01";
        String endDate = month + "-31";
        List<Map<String, Integer>> mapList = orderSettingDao.getOrderSettingByMonth(starDate, endDate);
        return mapList;
    }

    /**
     * 修改用户预约的设置信息
     * @param orderSetting
     */
    @Override
    @Transactional
    public void updateUsersData(OrderSetting orderSetting) throws HealthException {
        DataUtils(orderSetting);
    }

    /**
     * 更新用户预约的工具类（自己提取的一个方法）
     * @param orderSetting
     * @throws HealthException
     */
    public void DataUtils(OrderSetting orderSetting) throws HealthException {
        // 通过日期查询预约设置信息
        OrderSetting osInDB = orderSettingDao.findByOrderData(orderSetting.getOrderDate());
        // 存在
        if (null != osInDB) {
            //    是否 要设置的最大预约数量 < 已预约数量
            if (orderSetting.getNumber() < osInDB.getReservations()) {
                //       是 抛出异常
                throw new HealthException("最大预约数量不能小于已预约数量");
            }
            //       否，通过日期更新最大预约数
            orderSettingDao.updateNumber(orderSetting);
        } else {
            // 不存在
            //   插入预约设置
            orderSettingDao.add(orderSetting);
        }
    }
}
