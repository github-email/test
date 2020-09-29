package com.itheima.service;

import com.itheima.domain.Member;

/**
 * @Author：hushiqi
 * @Date：2020/9/29 11:41
 */
public interface MemberService {
    /**
     * 通过手机号查询会员
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
