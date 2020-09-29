package com.itheima.jop;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.domain.Setmeal;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiNiuUtils;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author：hushiqi
 * @Date：2020/9/25 17:48
 */
@Component
public class GenerateHtmlJob {
    /** 日志 */
    private static final Logger log = LoggerFactory.getLogger(GenerateHtmlJob.class);

    /**
     * spring创建对象后，调用的初始化方法
     */
    @PostConstruct
    public void init(){
    // 设置模板所在
        configuration.setTemplateLoader(new ClassTemplateLoader(this.getClass(),"/freemarker"));
    // 指定默认编码
        configuration.setDefaultEncoding("UTF-8");
    }
    /** jedis连接池 */
    @Autowired
    private JedisPool jedisPool;

    /** 订阅套餐服务 */
    @Reference
    private SetmealService setmealService;

    /** 注入freemarker主配置类 */
    @Autowired
    private Configuration configuration;

    /** 生成静态页面存放的目录 */
    @Value("${out_put_path}")
    private String out_put_path;

    /**
     * initialDelay 延迟多长时间后启动
     * fixedDelay: 间隔时间后执行
     */
    @Scheduled(initialDelay = 3000,fixedDelay = 1800000)
    public void doGenerateHtml(){
        //获取jedis连接
        Jedis jedis = jedisPool.getResource();
        // redis中set集合的key(只是为了见名之意，然别人一眼看出你这一个key是干嘛的)
        String key = "setmeal:static:html";
        //从redis中获取要处理的套餐id集合
        Set<String> smemberIds = jedis.smembers(key);
        //判断集合是否为空
        if(smemberIds!=null&&smemberIds.size()>0){
            //有数据需要处理
            for (String smemberId : smemberIds) {//双斜杠表示转义
                String[] setmealInfo = smemberId.split("\\|");
                //5. 套餐的id, 查询套餐详情
                String sid = setmealInfo[0]; // 套餐的id
                String oper = setmealInfo[1]; // 操作符 0删除，1生成静态页面(套餐被删除后，不需要在进行生成)
                //需要生成套餐详情页面的操作
                if ("1".equals(oper)){
                    //查询套餐详情
                    Setmeal setmealDetail = setmealService.findDetailById(Integer.valueOf(sid));
                    //设置图片的完整路径
                    setmealDetail.setImg(QiNiuUtils.DOMAIN+setmealDetail.getImg());
                    //创建数据模型
                    Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("setmeal",setmealDetail);
                    try {
                        //获取模板对象
                        String filename = String.format("%s/setmeal_%d.html", out_put_path, setmealDetail.getId());
                        generateHtml("mobile_setmeal_detail.ftl", filename, hashMap);
                        //删除对应的套餐的id
                        jedis.srem(key,smemberId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    //删除文件
                    String filename = String.format("%s/setmeal_%s.html", out_put_path, sid);
                    new File(filename).delete();
                    //删除对应的套餐id
                    jedis.srem(key,setmealInfo);
                }
            }
            //生成列表页面
            generateSetmealList();
        }
    }

    /**
     * 生成套餐列表页面
     */
    private void generateSetmealList(){
        // 1.查询所有套餐信息
        List<Setmeal> setmealList = setmealService.findAll();
        // 2.设置套餐完整图片路径
        setmealList.forEach(setmeal -> {
            setmeal.setImg(QiNiuUtils.DOMAIN + setmeal.getImg());
        });
        // 3.获取模板文件名
        String templateName = "mobile_setmeal.ftl";
        // 4. 生成 的文件名
        String filename = String.format("%s/mobile_setmeal.html",out_put_path);
        // 5. 构建数据模型
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("setmealList",setmealList);
        // 6. 生成html
        generateHtml(templateName,filename,dataMap);
    }

    private void generateHtml(String templateName,String filename,Map<String,Object> dataMap ){
        try {
            Template template = configuration.getTemplate(templateName);
            //9. 创建writer, 【注意】utf-8
            BufferedWriter writer =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"utf-8"));
            //10. 填充模板
            template.process(dataMap,writer);
            //11. 关闭writer
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
