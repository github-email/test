package com.itheima.exception;

/**
 * Description: 自定义异常
 *  友好提示
 *  区分系统与自定义的异常
 *  终止已经不符合业务逻辑的代码
 * @Author：hushiqi
 * @Date：2020/9/20 19:35
 */
public class HealthException extends RuntimeException{
    public HealthException(String message) {
        super(message);
    }
}
