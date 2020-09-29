package com.itheima.controller;

import com.alibaba.druid.util.StringUtils;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Author：hushiqi
 * @Date：2020/9/26 20:10
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    //定义log
    private static final Logger log = LoggerFactory.getLogger(ValidateCodeController.class);

    //注入JedisPool
    @Autowired
    private JedisPool jedisPool;

    /**
     * 检查预约的验证码
     */
    @RequestMapping("/send4Order")
    public Result sendToCode(String telephone) {//前端发送请求的变量名一致
        //1.获取jedis对象
        Jedis jedis = jedisPool.getResource();
        //定义key
        String key = RedisMessageConstant.SENDTYPE_ORDER + "_" + telephone;
        //获取redis中的验证码
        String codeInRedis = jedis.get(key);
        //判断是否发送
        if (!StringUtils.isEmpty(codeInRedis)) {
            //存在，发送过了
            return new Result(false, "验证码已发送，请注意查收!");
        }
        //不存在，生成验证码
        String validateCode = ValidateCodeUtils.generateValidateCode(6) + "";
        try {
            log.debug("给手机号码：{}发送验证码：{}", telephone, validateCode);
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, validateCode);
            log.debug("给手机号码：{}发送验证码：{} 发送成功", telephone, validateCode);
            //将验证码存入redis，设置有效时长10分钟
            jedis.setex(key, 60 * 10, validateCode);
            //返回结果
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(String.format("给手机号码:%s 发送验证码:%s 发送失败", telephone, validateCode), e);
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        } finally {
            jedis.close();
        }
    }

    /**
     * 手机登录的验证码
     *
     * @param telephone
     * @return
     */
    @PostMapping("/send4Login")
    public Result send4Login(String telephone) {
        //创建redis对象
        Jedis jedis = jedisPool.getResource();
        //获取key
        String key = RedisMessageConstant.SENDTYPE_LOGIN + "_" + telephone;
        //获取登录验证码
        String loginCode = jedis.get(key);
        //判断验证码是否已经发送
        if (!StringUtils.isEmpty(loginCode)) {
            return new Result(false, "验证码已发送，请注意查收!");
        }
        //不存在，生成验证码
        String telePhoneCode = ValidateCodeUtils.generateValidateCode(6) + "";
        try {
            //给用户发送验证码
            log.debug("给手机号码：{}发送验证码：{}", telephone, telePhoneCode);
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, telePhoneCode);
            log.debug("给手机号码：{}发送验证码：{} 发送成功", telephone, telePhoneCode);
            //设置验证码存放在redis中的时长为10分钟
            jedis.setex(key,10*60,telephone);
            //返回验证码发送成功的信息
            return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
//            e.printStackTrace();
            //打印发送失败的log日志信息
            log.debug(String.format("给手机号码:%s 发送验证码:%s 发送失败",telephone,telePhoneCode),e);
            return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }finally {
            //关闭jedis
            jedis.close();
        }
    }


}
