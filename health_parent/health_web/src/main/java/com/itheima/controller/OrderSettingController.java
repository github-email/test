package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.domain.OrderSetting;
import com.itheima.entity.Result;
import com.itheima.service.OrderSettingService;
import com.itheima.utils.POIUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author：hushiqi
 * @Date：2020/9/24 9:44
 */
@RestController
@RequestMapping("/orderSetting")
public class OrderSettingController {

    @Reference
    private OrderSettingService orderSettingService;

    @PostMapping("/upload")
    public Result upload(MultipartFile excelFile) {
        try {
            //读取excel文件每一行的内容
            List<String[]> readExcel = POIUtils.readExcel(excelFile);
            //日期格式解析
            SimpleDateFormat sdf = new SimpleDateFormat(POIUtils.DATE_FORMAT);
            //将存到集合中去
            List<OrderSetting> orderList = new ArrayList<>();
            //遍历读取到的内容
            for (String[] strings : readExcel) {
                //excel的单元格和行的下标都是从0开始
                Date orderDate = sdf.parse(strings[0]);
                int num = Integer.parseInt(strings[1]);
                OrderSetting orderSetting = new OrderSetting(orderDate, num);
                orderList.add(orderSetting);
            }
            //调用业务层
            orderSettingService.add(orderList);
            //返回结果
            return new Result(true,MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
    }

    /**
     * 通过月份获取预约设置的数据
     * @param month
     * @return
     */
    @GetMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String month){
        //调用业务层
        List<Map<String,Integer>> settingMapList = orderSettingService.getOrderSettingByMonth(month);
        //返回查询的结果
        return new Result(true,MessageConstant.GET_ORDERSETTING_SUCCESS,settingMapList);
    }

    /**
     * 修改预约设置信息
     * @param orderSetting
     * @return
     */
    @PostMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        //调用服务层更新用户预约信息
        orderSettingService.updateUsersData(orderSetting);
        return new Result(true,MessageConstant.ORDERSETTING_SUCCESS);
    }
}
