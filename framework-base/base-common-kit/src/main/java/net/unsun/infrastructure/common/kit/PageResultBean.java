package net.unsun.infrastructure.common.kit;

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
    public String toString() {
        return "PageResultBean{" +
                "pagination=" + pagination +
                ", list=" + list +
                '}';
    }
}
