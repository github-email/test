package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.domain.CheckGroup;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.service.CheckGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author：hushiqi
 * @Date：2020/9/20 20:17
 */
@RestController
@RequestMapping("/checkGroup")
public class CheckGroupController {

    @Reference
    private CheckGroupService checkGroupService;

    @PostMapping("/findAll")
    public Result findAll(){
        List<CheckGroup> checkItemList = checkGroupService.findAll();
        //MessageConstant这是一个消息常量接口
        Result result = new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, checkItemList);
        return result;
    }
    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult<CheckGroup> PageResult = checkGroupService.findPage(queryPageBean);
        //返回结果
        Result result = new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, PageResult);
        return result;
    }

    /**
     * 添加
     * @param checkGroup
     * @param checkitemIds
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds){
        //调用业务层
        checkGroupService.add(checkGroup,checkitemIds);
        //响应结果
        Result result = new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
        return result;
    }

    /**
     * 通过id进行查询
     * @param id
     * @return
     */
    @PostMapping("/findById")
    public Result findById(Integer id){
        //调用业务层
        CheckGroup checkGroup = checkGroupService.findById(id);
        //返回查询的结果
        return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroup);
    }

    /**
     * 通过检查组id查询选中的检查项id
     * @param id
     * @return
     */
    @PostMapping("/findCheckItemIdsToCheckGroupId")
    public Result findCheckItemIdsToCheckGroupId(Integer id){
        //调用业务层
        List<CheckGroup> checkGroupListIds = checkGroupService.findCheckItemIdsToCheckGroupId(id);
        //返回查询的结果
        return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroupListIds);
    }

    /**
     * 修改
     * @param checkGroup
     * @param checkItemsIds
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody CheckGroup checkGroup,Integer[] checkItemsIds){
        //调用业务层
        checkGroupService.update(checkGroup,checkItemsIds);
        //返回更新的结果
        return new Result(true,MessageConstant.EDIT_CHECKGROUP_SUCCESS);
    }

    /**
     * 删除(有点小问题，删除存在的，异常没有捕获)
     * @param id
     * @return
     */
    @GetMapping("/deleteById")
    public Result deleteById(Integer id){
        //调用业务层
        checkGroupService.deleteById(id);
        //返回删除后的结果
        return new Result(true,MessageConstant.DELETE_CHECKGROUP_SUCCESS);
    }
}
