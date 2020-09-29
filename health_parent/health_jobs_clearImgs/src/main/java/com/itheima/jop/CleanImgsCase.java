package com.itheima.jop;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiNiuUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author：hushiqi
 * @Date：2020/9/23 15:10
 */

/**
 * 删除七牛云上的无用图片
 */
@Component("cleanImgsCase")
public class CleanImgsCase {

    @Reference
    private SetmealService setmealService;

    public void cleanImgs(){
        //1.获取七牛云上的所有图片
        List<String> imgs7NiuList = QiNiuUtils.listFile();
        //2.获取数据中所有套餐的所有图片
        List<String> ImgsDbList = setmealService.findImgs();
        //3.七牛图片减去数据库图片
        imgs7NiuList.removeAll(ImgsDbList);
        System.out.println("删除的是什么："+imgs7NiuList.removeAll(ImgsDbList));
        //4.删除数据
        String[] strings = imgs7NiuList.toArray(new String[]{});
        QiNiuUtils.removeFiles(strings);
    }
}
