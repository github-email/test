package com.itheima.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.domain.Order;
import com.itheima.entity.Result;
import com.itheima.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * @Author：hushiqi
 * @Date：2020/9/28 20:24
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;

    @Autowired
    private JedisPool jedisPool;

    /**
     * 提交预约
     * @param orderInfo
     * @return
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String,String> orderInfo){
        // 验证校验
        //    验证前端提交过来的验证码与redis的验证码是否一致
        //从redis中获取验证码对应的key,即手机号
        String keyPhone = RedisMessageConstant.SENDTYPE_ORDER + "_" + orderInfo.get("telephone");
        //获取redis对象
        Jedis jedis = jedisPool.getResource();
        //获取redis中key对应的验证码
        String redisCode = jedis.get(keyPhone);
        //判断redis中获取的验证码是否为空？
        if (StringUtils.isEmpty(redisCode)){
            //为空提示用户获取验证码
            return new Result(false,"请获取验证码");
        }
        if (!redisCode.equalsIgnoreCase(orderInfo.get("validateCode"))){
            //不一样返回验证码错误
            return new Result(false,MessageConstant.VALIDATECODE_ERROR);
        }
        //防止重复提交，移除redis中的验证码
        jedis.del(keyPhone);
        // 设置预约类型 health_mobile给手机端微信公众号使用的，写死它的类型为微信预约
        orderInfo.put("orderType", Order.ORDERTYPE_WEIXIN);
        //一样,则调用服务层
        Order order = orderService.submit(orderInfo);
        //返回信息给页面
        return new Result(true,MessageConstant.ORDER_SUCCESS,order);
    }


    /**
     * 预约成功后，展示的详细信息，即订单详情
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Result findById(Integer id){
        //调用业务层
        Map<String,String> orderInfo = orderService.findById(id);
        //返回数据给页面
        return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,orderInfo);
    }
}
