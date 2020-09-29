package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.domain.Member;
import com.itheima.domain.Order;
import com.itheima.domain.OrderSetting;
import com.itheima.exception.HealthException;
import com.itheima.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author：hushiqi
 * @Date：2020/9/28 20:54
 */
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Autowired
    private MemberDao memberDao;

    /**
     * 提交预约
     *
     * @param orderInfo
     * @return
     */
    @Override
    public Order submit(Map<String, String> orderInfo) throws HealthException {
        //1. 通过日期查询预约设置信息
        //创建格式化对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            //获取从前端传过来的预约日期
            date = sdf.parse(orderInfo.get("orderDate"));
        } catch (Exception e) {
//            e.printStackTrace();
            throw new HealthException("日期格式不正确，请选择正确的日期");
        }
        //从订单信息中获取日期
        OrderSetting orderSettingDate = orderSettingDao.findByOrderData(date);
        //判断日期是否为空
        if (orderSettingDate == null){
            //为空，则报错
            throw new HealthException("所选日期不能预约，请选择其它日期");
        }
        //存在：1.判断预约是否已满
        if (orderSettingDate.getReservations() >= orderSettingDate.getNumber()){
            //已满，报错
            throw new HealthException("选日期预约已满，请选择其它日期");
        }
        //判断是否重复预约
        //获取手机号
        String telephone = orderInfo.get("telephone");
        //通过手机号获取会员信息
        Member member = memberDao.findByTelephone(telephone);
        Order order = new Order();
        order.setOrderDate(date);
        order.setSetmealId(Integer.valueOf(orderInfo.get("setmealId")));
        if(null != member) {
            //     查询t_order, 条件orderDate=? and setmeal_id=?,member=?
            order.setMemberId(member.getId());
            //判断是否重复预约
            List<Order> orderList = orderDao.findByCondition(order);
            if(null != orderList && orderList.size() > 0){
                throw new HealthException("该套餐已经预约过了，请不要重复预约");
            }
        }else {
            //不存在
            member = new Member();
            // name 从前端来
            member.setName(orderInfo.get("name"));
            // sex  从前端来
            member.setSex(orderInfo.get("sex"));
            // idCard 从前端来
            member.setIdCard(orderInfo.get("idCard"));
            // phoneNumber 从前端来
            member.setPhoneNumber(telephone);
            // regTime 系统时间
            member.setRegTime(new Date());
            // password 可以不填，也可生成一个初始密码
            member.setPassword("12345678");
            // remark 自动注册
            member.setRemark("由预约而注册上来的");
            //   添加会员
            memberDao.add(member);
            order.setMemberId(member.getId());
        }
        //3. 可预约
        // 预约类型
        order.setOrderType(orderInfo.get("orderType"));
        // 预约状态
        order.setOrderStatus(Order.ORDERSTATUS_NO);
        //添加t_order 预约信息,添加订单
        orderDao.add(order);
        //4. 更新已预约人数, 更新成功则返回1，数据没有变更则返回0
        int affectedCount = orderSettingDao.editReservationsByOrderDate(orderSettingDate);
        if(affectedCount == 0){
            throw new HealthException(MessageConstant.ORDER_FULL);
        }
        //5. 返回新添加的订单对象
        return order;
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, String> findById(Integer id) {
        return orderDao.findById(id);
    }
}
