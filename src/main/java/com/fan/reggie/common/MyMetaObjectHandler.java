package com.fan.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 使用
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
      log.info("开始对插入数据进行数据填充");
        log.info("当前线程id：{}",Thread.currentThread().getId());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentUserId());
        metaObject.setValue("updateUser", BaseContext.getCurrentUserId());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始对修改数据进行数据填充");
        log.info("当前线程id：{}",Thread.currentThread().getId());
        metaObject.setValue("updateUser", BaseContext.getCurrentUserId());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }
}
