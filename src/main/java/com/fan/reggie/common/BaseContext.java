package com.fan.reggie.common;

/**
 * 每发送一个请求代表一个线程，在一次请求中可以利用线程获取数据，例如本项目中，对数据insert和update操作中，使用线程来获取用户id，当然需要提前往线程里放
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal  = new ThreadLocal<>();

    public static void setCurrentUserId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentUserId(){
        return threadLocal.get();
    }
}
