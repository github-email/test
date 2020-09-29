package com.itheima.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.CheckItemDao;
import com.itheima.domain.CheckItem;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.HealthException;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author：hushiqi
 * @Date：2020/9/18 18:23
 */
@Service(interfaceClass = CheckItemService.class)
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;

    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult<CheckItem> findPage(QueryPageBean queryPageBean) {
        /**
         * PageHelper 表示分页插件
         * 开始分页
         * startPage
         * @param pageNum  表示当前的页码
         * @param pageSize 每页显示数量
         */
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //判断是否有查询条件
        /**
         * @param  QueryString  表示查询的条件
         */
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())){
            //有查询条件，拼接查询条件(模糊查询)
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        //查询的数据进行分页
        Page<CheckItem> page = checkItemDao.findPage(queryPageBean.getQueryString());
        //封装到分页结果对象中
        PageResult<CheckItem> pageResult = new PageResult<>(page.getTotal(), page.getResult());
        return pageResult;
    }

    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    @Override
    public void update(CheckItem checkItem) {
            checkItemDao.update(checkItem);
    }

    @Override
    public void deleteById(Integer id) throws HealthException {
        // 判断是否被检查组使用了
        // 统计个数检查组与检查项的关系表 条件检查的id=id
        int count = checkItemDao.findCountByCheckItemId(id);
        // 被用了，就要报错
        if(count > 0){
            throw new HealthException("该检查项被检查组使用了，不能删除");
        }
        // 没被用，则可以删除
        checkItemDao.deleteById(id);
    }
}
