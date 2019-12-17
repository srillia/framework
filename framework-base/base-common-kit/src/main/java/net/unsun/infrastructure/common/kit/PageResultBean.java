package net.unsun.infrastructure.common.kit;

import net.unsun.infrastructure.common.constant.BaseCode;

import java.util.List;

/**
 * 分页的结果集封装
 *
 * @author Tokey
 * @version 1.0.0
 * @since 2017/09/05 11:26
 */
public class PageResultBean<T> extends ResultBean<T> {

    /**
     * 分页信息
     */
    private PageBean pagination;

    /**
     * 分页数据
     */
    private List<T> list;

    public PageResultBean() {
        super();
    }

    /**
     * 创建当前对象
     *
     * @return 当前对象
     */
    public static PageResultBean create() {
        return new PageResultBean();
    }


    /**
     * 返回成功结果集对象
     *
     * @return 当前对象
     */
    public static PageResultBean success() {
        return new PageResultBean().setCode(BaseCode.success);
    }

    /**
     * 返回失败结果集对象
     *
     * @return 当前对象
     */
    public static PageResultBean fail() {
        return new PageResultBean().setCode(BaseCode.fail).setSuccess(false);
    }

    /**
     * 返回空结果集对象
     *
     * @return 当前对象
     */
    public static PageResultBean blank() {
        return new PageResultBean().setCode(BaseCode.notFound).setSuccess(false);
    }

    /**
     * 返回系统繁忙结果集对象
     *
     * @return 当前对象
     */
    public static PageResultBean systemBusy() {
        return new PageResultBean().setCode(BaseCode.systemBusy).setSuccess(false);
    }

    /**
     * 返回异常结果集对象
     *
     * @return 当前对象
     */
    public static PageResultBean exception() {
        return new PageResultBean().setCode(BaseCode.exception).setSuccess(false);
    }

    /**
     * 返回返回未登录结果集对象
     *
     * @return 当前对象
     */
    public static PageResultBean notlogin() {
        return new PageResultBean().setCode(BaseCode.notLogin).setSuccess(false);
    }

    /**
     * 返回返回认证失败结果集对象
     *
     * @return 当前对象
     */
    public static PageResultBean authFeiled() {
        return new PageResultBean().setCode(BaseCode.authFeiled).setSuccess(false);
    }

    /**
     * 返回返回没有授权结果集对象
     *
     * @return 当前对象
     */
    public static PageResultBean notPermited() {
        return new PageResultBean().setCode(BaseCode.notPermited).setSuccess(false);
    }





    public PageBean getPagination() {
        return pagination;
    }

    public PageResultBean setPagination(PageBean pagination) {
        this.pagination = pagination;
        return this;
    }

    public List<T> getList() {
        return list;
    }

    public PageResultBean<T> setList(List<T> list) {
        this.list = list;
        return this;
    }

    @Override
    public PageResultBean<T> setCode(BaseCode baseCode) {
        super.setCode(baseCode);
        return this;
    }

    @Override
    public PageResultBean<T> setMessage(String message) {
        super.setMessage(message);
        return this;
    }

    @Override
    public PageResultBean<T> setData(T data) {
        super.setData(data);
        return this;
    }

    @Override
    public PageResultBean<T> setSuccess(Boolean success) {
        super.setSuccess(success);
        return this;
    }

    @Override
    public String toString() {
        return "PageResultBean{" +
                "pagination=" + pagination +
                ", list=" + list +
                '}';
    }
}
