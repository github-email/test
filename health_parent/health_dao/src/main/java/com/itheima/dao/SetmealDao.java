package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.domain.Setmeal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author：hushiqi
 * @Date：2020/9/22 11:50
 */
public interface SetmealDao {
    Page<Setmeal> findPage(String queryString);

    void add(Setmeal setmeal);

    /**
     * 由于param通过反射只能拿到对应的属性字段的类型不能拿到对应的值，而他们两个参数的名称又是一样的所以无法区分
     * @param setmealId
     * @param checkgroupId
     */
    void addSetmealAndCheckGroup(@Param("setmealId") Integer setmealId, @Param("checkgroupId") Integer checkgroupId);

    void update(Setmeal setmeal);

    List<Setmeal> findByCheckGroupIdToSetmealId(Integer id);

    Setmeal findById(Integer id);

    /**
     * 删除套餐与检查组的关系
     * @param id
     */
    void deleteSetmealCheckGroup(Integer id);

    /**
     * 删除套餐
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 通过套餐的id统计订单的个数
     * @param id
     * @return
     */
    int findCountBySetmealId(Integer id);

    List<String> findImgs();

    List<Setmeal> findAll();

    Setmeal findDetailById(Integer id);
}
