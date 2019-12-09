package net.unsun.infrastructure.mybatisplus.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * 基础实体类
 *
 * @author srillia(srillia@coldweaponera.com)
 * @version 1.0.0
 * @since 2017-06-30 15:19
 */
public abstract class BaseEntity<T extends Model<T>> extends Model<T> {

}
