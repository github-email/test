package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.domain.Setmeal;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiNiuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author：hushiqi
 * @Date：2020/9/22 11:27
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    @Autowired
    private JedisPool jedisPool;

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean){
        //调用业务层
        PageResult<Setmeal> setmealPageResult = setmealService.findPage(queryPageBean);
        //返回数据
        return new Result(true,MessageConstant.QUERY_SETMEALLIST_SUCCESS,setmealPageResult);
    }

    /**
     * 文件上传
     * @param imgFile
     * @return
     */
    @PostMapping("/upload")//图片上传必须是post的请求方式，get请求大小的限制
    public Result upload(MultipartFile imgFile){//此处的名字必须与前端的名字一致，否则会报错
        //获取原图片的名字
        String originalFilename = imgFile.getOriginalFilename();
        //截取图片的名字，只需要图片的后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //通过UUID生成唯一的图片文件名
        UUID randomUUID = UUID.randomUUID();
        //拼接文件名和图片后缀
        String fileName = randomUUID+suffix;
        //通过七牛上传文件
        try {
            QiNiuUtils.uploadViaByte(imgFile.getBytes(),fileName);
            //创建一个map集合来存储图片名和值
            Map<String, String> ImgsMap = new HashMap<>();
            ImgsMap.put("imgName",fileName);
            ImgsMap.put("domain",QiNiuUtils.DOMAIN);//domain就是外接连域名
            //返回结果
            return new Result(true,MessageConstant.PIC_UPLOAD_SUCCESS,ImgsMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,MessageConstant.PIC_UPLOAD_FAIL);
    }

    /**
     * 添加套餐
     * @param setmeal
     * @param checkgroupIds
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        //调用业务层
        Integer setmealId = setmealService.add(setmeal, checkgroupIds);
        //添加redis，生成静态页面
        Jedis jedis = jedisPool.getResource();
        String key = "setmeal:static:html";
        //符号“|”表示通过该符号进行分割，1表示生成静态页面，后面的就是一个时间戳
        jedis.sadd(key,setmealId+""+"|1|"+System.currentTimeMillis());
        //关闭jedis
        jedis.close();
        //返回添加的结果
        return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    /**
     * 通过id查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Result findById(Integer id){
        //调用业务层
        Setmeal setmeal = setmealService.findById(id);
        //表单中没有图片的数据,图片的路径问题，需要返回给前端
        Map<String, Object> stringHashMap = new HashMap<>();
        stringHashMap.put("setmeal",setmeal);
        stringHashMap.put("domain",QiNiuUtils.DOMAIN);
        //返回数据
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,stringHashMap);
    }

    /**
     * 查询选中的检查组id集合
     * @return
     */
    @GetMapping("/findByCheckGroupIdToSetmealId")
    public Result findByCheckGroupIdToSetmealId(Integer id){
        //调用业务层
        List<Setmeal> setmealList = setmealService.findByCheckGroupIdToSetmealId(id);
        //返回选中检查组的结果id
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmealList);
    }

    /**
     * 更新套餐
     * @param setmeal
     * @param checkGroupIds
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Setmeal setmeal,Integer[] checkGroupIds){
        //调用业务层
        setmealService.update(setmeal,checkGroupIds);
        //添加redis，生成静态页面
        Jedis jedis = jedisPool.getResource();
        String key = "setmeal:static:html";
        //符号“|”表示通过该符号进行分割，1表示生成静态页面，后面的就是一个时间戳
        jedis.sadd(key,setmeal.getId()+""+"|1|"+System.currentTimeMillis());
        //关闭jedis
        jedis.close();
        //返回修改的结果
        return new Result(true, MessageConstant.EDIT_SETMEAL_SUCCESS);
    }

    // 删除套餐
    @RequestMapping(value = "/delete")
    public Result delete(Integer id){
        setmealService.deleteById(id);
        //添加redis，生成静态页面
        Jedis jedis = jedisPool.getResource();
        String key = "setmeal:static:html";
        //符号“|”表示通过该符号进行分割，1表示生成静态页面，后面的就是一个时间戳
        jedis.sadd(key,id+""+"|0|"+System.currentTimeMillis());
        //关闭jedis
        jedis.close();
        return new Result(true, "删除套餐成功!");
    }
}
