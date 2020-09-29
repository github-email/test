package com.itheima.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.SetmealDao;
import com.itheima.domain.Setmeal;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.HealthException;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author：hushiqi
 * @Date：2020/9/22 11:39
 */
@Service(interfaceClass = SetmealService.class)
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealDao setmealDao;

    @Override
    public PageResult<Setmeal> findPage(QueryPageBean queryPageBean) {
        //设置当前页和每页大小
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //判断是否有查询条件
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())){
            //有调条件,进行模糊查询
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        //分页查询
        Page<Setmeal> setmealPage = setmealDao.findPage(queryPageBean.getQueryString());
        //返回查询的结果(结果包含数据和总条数)
        PageResult<Setmeal> pageResult = new PageResult<>(setmealPage.getTotal(), setmealPage.getResult());
        return pageResult;
    }

    @Override
    @Transactional
    public Integer add(Setmeal setmeal, Integer[] checkgroupIds) {
        //调用dao层
        setmealDao.add(setmeal);
        //获取套餐id
        Integer setmealId = setmeal.getId();
        //通过检查组id判断套餐与检查组的关系
        if (null != checkgroupIds){
            //遍历数组
            for (Integer checkgroupId : checkgroupIds) {
                //添加套餐和检查组的关系
                setmealDao.addSetmealAndCheckGroup(setmealId,checkgroupId);
            }
        }
        return setmealId;
    }

    /**
     * 通过id查询套餐信息
     * @param id
     * @return
     */
    @Override
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    /**
     * 查询选中的检查组id集合
     * @return
     */
    @Override
    public List<Setmeal> findByCheckGroupIdToSetmealId(Integer id) {
        return setmealDao.findByCheckGroupIdToSetmealId(id);
    }

    /**
     * 更新套餐
     * @param setmeal
     * @param checkGroupIds
     * @return
     */
    @Override
    public void update(Setmeal setmeal, Integer[] checkGroupIds) {
        setmealDao.update(setmeal);
        if (null!= checkGroupIds){
            for (Integer checkGroupId : checkGroupIds) {
                //添加套餐和检查组的关系
                setmealDao.addSetmealAndCheckGroup(setmeal.getId(),checkGroupId);
            }
        }
    }


    // 删除套餐
    /**
     * 删除套餐
     * @param id
     * @throws HealthException
     */
    @Override
    @Transactional
    public void deleteById(Integer id) throws HealthException {
        // 判断 是否被订单使用
        int count = setmealDao.findCountBySetmealId(id);
        // 使用了则报错
        if(count > 0) {
            throw new HealthException("该套餐已经被订单使用了，不能删除!");
        }
        // 没使用
        // 先删除套餐与检查组的关系
        setmealDao.deleteSetmealCheckGroup(id);
        // 再删除套餐
        setmealDao.deleteById(id);
    }

    @Override
    public List<String> findImgs() {
        return setmealDao.findImgs();
    }

    /**
     *查询所有的套餐
     * @return
     */
    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    /**
     * 查询套餐详情
     * @param id
     * @return
     */
    @Override
    public Setmeal findDetailById(Integer id) {
        return setmealDao.findDetailById(id);
    }
}
