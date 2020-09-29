package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.domain.Setmeal;
import com.itheima.entity.Result;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiNiuUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author：hushiqi
 * @Date：2020/9/24 17:37
 */
@RestController
@RequestMapping("/setmeal")
public class MobileController {

    @Reference
    private SetmealService setmealService;

    @GetMapping("/getSetmeal")
    public Result getSetmeal(){
        //查询所有套餐
        List<Setmeal> setmealList = setmealService.findAll();

        // 套餐里有图片有全路径吗? 拼接全路径
        setmealList.forEach(s->{
            s.setImg(QiNiuUtils.DOMAIN + s.getImg());
        });
        //返回数据
        return new Result(true,MessageConstant.GET_SETMEAL_LIST_SUCCESS,setmealList);
    }

    @GetMapping("/findDetailById")
    public Result findDetailById(Integer id){
        //调用业务层
        Setmeal setmeal = setmealService.findDetailById(id);
        //拼接图片的完整路径
        setmeal.setImg(QiNiuUtils.DOMAIN+setmeal.getImg());
        //返回查询的结果
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    }

    /**
     * 查询套餐信息
     * @param id
     * @return
     */
    @PostMapping("/findById")
    public Result findById(Integer id){
        //调用业务层
        Setmeal setmeal = setmealService.findById(id);
        //拼接图片的完整路径
        setmeal.setImg(QiNiuUtils.DOMAIN+setmeal.getImg());
        //返回查询的结果
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    }
}
