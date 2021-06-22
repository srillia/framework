package io.gaad.infrastructure.security.ibatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import io.gaad.infrastructure.security.base.UserDetail;
import io.gaad.infrastructure.security.util.SecurityKit;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * @program: unsun-framework
 * @author: Tokey
 * @create: 2019-12-04 16:18
 */
@Slf4j
public class CustomMetaObjectHandler implements MetaObjectHandler {


    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.setFieldValByName("createDatetime", LocalDateTime.now(), metaObject);//版本号3.0.6以及之前的版本
        this.setFieldValByName("updateDatetime", LocalDateTime.now(), metaObject);//版本号3.0.6以及之前的版本
        UserDetail userDetail = SecurityKit.currentUser();
        if(userDetail != null && userDetail.getUserId() != null) {
            this.setFieldValByName("createById", userDetail.getUserId(), metaObject);//版本号3.0.6以及之前的版本
            this.setFieldValByName("createByName", userDetail.getUsername(), metaObject);//版本号3.0.6以及之前的版本
            this.setFieldValByName("updateById", userDetail.getUserId(), metaObject);//版本号3.0.6以及之前的版本
            this.setFieldValByName("updateByName", userDetail.getUsername(), metaObject);//版本号3.0.6以及之前的版本
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.setFieldValByName("updateDatetime", LocalDateTime.now(), metaObject);
        UserDetail userDetail = SecurityKit.currentUser();
        if(userDetail != null && userDetail.getUserId() != null) {
            this.setFieldValByName("updateById", userDetail.getUserId(), metaObject);//版本号3.0.6以及之前的版本
            this.setFieldValByName("updateByName", userDetail.getUsername(), metaObject);//版本号3.0.6以及之前的版本
        }
    }
}