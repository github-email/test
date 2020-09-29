package com.itheima.dao;

import com.itheima.domain.Member;

/**
 * @Author：hushiqi
 * @Date：2020/9/28 21:27
 */
public interface MemberDao {
    /**
     * 通过手机号码查询会员信息
     * @param telephone
     * @return
     */
    Member findByTelephone(String telephone);

    /**
     * 添加会员
     * @param member
     */
    void add(Member member);
}
