package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.domain.CheckItem;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.constant.MessageConstant;
import com.itheima.service.CheckItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Author：hushiqi
 * @Date：2020/9/18 18:12
 */
@RestController
@RequestMapping("/check")
public class CheckItemController {

    @Reference
    private CheckItemService checkItemService;

    @PostMapping("/findAll")
    public Result findAll(){
        List<CheckItem> checkItemList = checkItemService.findAll();
        //MessageConstant这是一个消息常量接口
        Result result = new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS, checkItemList);
        return result;
    }

    @PostMapping("/add")
    public Result add(@RequestBody CheckItem checkItem){
        //获取参数
        checkItemService.add(checkItem);
        return new Result(true,MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean){
        //调用业务层,获取要分页的数据的结果
        PageResult<CheckItem> pageResult =  checkItemService.findPage(queryPageBean);
        //返回结果
        return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,pageResult);
    }

    /**
     * 通过id进行查询
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Result findById(Integer id){
            CheckItem checkItem = checkItemService.findById(id);
            //返回数据
        return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItem);
    }

    /**
     * 修改
     * @param checkItem
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody CheckItem checkItem){
        checkItemService.update(checkItem);
        return new Result(true,MessageConstant.EDIT_CHECKITEM_SUCCESS);
    }

    /**
     * 通过id进行删除
     * @param id
     * @return
     */
    @PostMapping("/deleteById")
    public Result deleteById(Integer id){
        checkItemService.deleteById(id);
        return new Result(true,MessageConstant.DELETE_CHECKITEM_SUCCESS);
    }
}
