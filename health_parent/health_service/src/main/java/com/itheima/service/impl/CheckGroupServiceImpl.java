package com.itheima.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.CheckGroupDao;
import com.itheima.domain.CheckGroup;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.HealthException;
import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author：hushiqi
 * @Date：2020/9/20 20:24
 */
@Service(interfaceClass = CheckGroupService.class)
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    private CheckGroupDao checkGroupDao;

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult<CheckGroup> findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())){
            //不为空，进行模糊查询
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        //进行数据的分页
        Page<CheckGroup> page = checkGroupDao.findPage(queryPageBean.getQueryString());
        PageResult<CheckGroup> pageResult = new PageResult<>(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 添加数据
     * @param checkGroup
     * @param checkitemIds
     */
    @Override
    @Transactional//事务注解
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //添加检查组
        checkGroupDao.add(checkGroup);
        //获取检查组id
        Integer groupId = checkGroup.getId();
        if (null != checkitemIds){
            for (Integer checkitemId : checkitemIds) {
                //添加检查组与检查项的关系
                checkGroupDao.addCheckGroupCheckItem(groupId, checkitemId);
            }
        }
    }

    /**
     * 通过id进行查询检查组数据
     * @param id
     * @return
     */
    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    /**
     * 通过检查组id查询选中的检查项id
     * @param id
     * @return
     */
    @Override
    public List<CheckGroup> findCheckItemIdsToCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsToCheckGroupId(id);
    }

    /**
     * 检查组修改
     * @param checkGroup
     * @param checkItemsIds
     */
    @Override
    @Transactional//添加事务
    public void update(CheckGroup checkGroup, Integer[] checkItemsIds) {

        //调用业务层,更新业务组数据
        checkGroupDao.update(checkGroup);
        //删除checkGroup和checkItem旧的关系
        checkGroupDao.deleteCheckGroupToCheckItem(checkGroup.getId());
        //遍历所有的checkItemsIds
        for (Integer checkItemsId : checkItemsIds) {
            if (checkItemsId != null){
                //添加checkGroup和checkItem新的关系
                checkGroupDao.addCheckGroupCheckItem(checkGroup.getId(),checkItemsId);
            }
        }
    }

    @Override
    @Transactional//添加事务的注解
    public void deleteById(Integer id) throws HealthException {
        //判断检查组是否被套餐使用了
        int cnt = checkGroupDao.findSetmealCountByCheckGroupId(id);
        if (cnt > 0){
        //使用了，则报错
            throw new HealthException("该检查组已经被套餐使用，不可以删除");
        }else {
        //没使用，则可以删除
        // 先删除 检查组与检查项的关系
            checkGroupDao.deleteCheckGroupToCheckItem(id);
        // 再删除检查组
            checkGroupDao.deleteById(id);
        }
    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }
}
