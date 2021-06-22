package io.gaad.infrastructure.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.gaad.infrastructure.mybatisplus.service.IBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础服务类
 *
 * @author srillia(srillia@coldweaponera.com)
 * @version 1.0.0
 * @since 2017-06-30 15:22
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IBaseService<T> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

}
