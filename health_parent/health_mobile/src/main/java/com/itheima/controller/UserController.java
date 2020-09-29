package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.domain.Member;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @Author：hushiqi
 * @Date：2020/9/29 9:03
 */
@RestController
@RequestMapping("/login")
public class UserController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;

    @PostMapping("/check")
    public Result check(@RequestBody Map<String,String> loginInfo, HttpServletResponse response){
        //获取用户的telePhone
        String telephone = loginInfo.get("telephone");
        //从redis中获取生成的验证码
        String key = RedisMessageConstant.SENDTYPE_LOGIN + "_" + telephone;
        Jedis jedis = jedisPool.getResource();
        String redisCode = jedis.get(key);
        //获取用户输入的验证码
        String validateCode = loginInfo.get("validateCode");
        //判断是否有发送验证码或者验证码是否失效
        if (redisCode == null){
            return new Result(false,"请输入手机号进行验证码的发送");
        }
        //判断用户输入的验证码和redis中的验证码是否一致
        if (redisCode.equalsIgnoreCase(validateCode)){
            jedis.del(key);// 清除验证码，已经使用过了
            //相等,判断是否是会员
            //通过手机号经判断
            Member member = memberService.findByTelephone(telephone);
            if (member == null){
                //会员不存在，添加会员信息
                member.setRegTime(new Date());
                member.setPhoneNumber(telephone);
                member.setRemark("手机号快速注册");
                memberService.add(member);
            }
        }else {
            //不相等
            return new Result(false,"验证码不正确");
        }
        //保存用户的手机号
        Cookie cookie = new Cookie("login_member_telephone", telephone);
        //设置最大存储时间
        cookie.setMaxAge(30*24*60*60);
        //设置访问的路径
        cookie.setPath("/");
        response.addCookie(cookie);
        //返回结果
        return new Result(true,MessageConstant.LOGIN_SUCCESS);
    }
}
