package net.unsun.infrastructure.common.kit;

import java.io.Serializable;
import java.util.Map;

/**
 * 分页JavaBean （参数）
 *
 * @author Tokey
 */
public class PageBean implements Serializable {

    /***
     * 当前页
     */
    private int curPage = 1;

    /***
     * 总记录数
     */
    private long totalRecord = 0;

    /***
     * 每页记录数
     */
    private int pageRecord = 20;

    /**
     * 排序字段与排序方式
     * 例如：a asc, b desc
     * 设定值方式：
     * a true ---> a asc
     * b false ---> b desc
     */
    private Map<String, Boolean> orderBy;

    /**
     * 实例当前对象
     */
    public static PageBean create() {
        return new PageBean();
    }

    /**
     * 获取排序方式
     */
    public Map<String, Boolean> getOrderBy() {
        return orderBy;
    }

    /**
     * 设置排序字段与排序方式
     * 例如：a asc, b desc
     * 设定值方式：
     * a true ---> a asc
     * b desc ---> b desc
     */
    public PageBean setOrderBy(Map<String, Boolean> orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    /**
     * 获取当前页码
     */
    public int getCurPage() {
        return curPage;
    }

    /**
     * 设置当前页码
     */
    public PageBean setCurPage(String curPage) {
        if (null == curPage || "".equals(curPage.trim())) {
            return this;
        }
        int acurPage = Integer.parseInt(curPage.trim());
        this.setCurPage(acurPage);
        return this;
    }

    /**
     * 设置当前页码
     */
    public PageBean setCurPage(int curPage) {
        if (curPage > 1) {
            this.curPage = curPage;
        }
        return this;
    }

    /**
     * 获取每页最大记录数
     */
    public int getPageRecord() {
        return pageRecord;
    }

    /**
     * 设置每页最大记录数
     */
    public PageBean setPageRecord(String pageSize) {
        if (null != pageSize && !"".equals(pageSize.trim())) {
            this.setPageRecord(Integer.parseInt(pageSize.trim()));
        }
        return this;
    }

    /**
     * 设置每页最大记录数
     */
    public PageBean setPageRecord(int pageRecord) {
        this.pageRecord = pageRecord;
        return this;
    }

    /**
     * 获取查询结果记录总数
     */
    public long getTotalRecord() {
        return totalRecord;
    }

    /**
     * 设置查询结果记录总数
     */
    public PageBean setTotalRecord(String totalRecord) {
        if (null != totalRecord && !"".equals(totalRecord.trim())) {
            this.totalRecord = Long.parseLong(totalRecord);
        }
        return this;
    }

    /**
     * 设置查询结果记录总数
     */
    public PageBean setTotalRecord(long totalRecord) {
        this.totalRecord = totalRecord;
        return this;
    }

    /**
     * 返回下一页页码
     */
    public long getNextPageNumber() {
        long nextPageNumber = this.curPage + 1;
        if (nextPageNumber > this.getMaxPageNumber()) {
            nextPageNumber = this.getMaxPageNumber();
        }
        return nextPageNumber;
    }

    /**
     * 返回上一页页码
     */
    public long getPreviousPageNumber() {
        long previousPageNumber = this.curPage - 1;
        if (previousPageNumber < 1) {
            previousPageNumber = 1;
        }
        return previousPageNumber;
    }

    /**
     * 返回最大页码编号
     */
    public long getMaxPageNumber() {
        long maxPageNumber = 1;
        if (this.totalRecord > this.pageRecord) {
            if (0 == this.totalRecord % this.pageRecord) {
                maxPageNumber = this.totalRecord / this.pageRecord;
            } else {
                maxPageNumber = this.totalRecord / this.pageRecord + 1;
            }
        }
        return maxPageNumber;
    }

    /**
     * 获取起始记录下标
     */
    public long getStartRecordIndex() {
        return this.pageRecord * (this.curPage - 1);
    }

    /**
     * 获取结束记录下标
     */
    public long getEndRecordIndex() {
        long end = this.pageRecord * this.curPage - 1;
        if (end > (this.totalRecord - 1)) {
            end = (this.totalRecord - 1);
        }
        return end;
    }

    @Override
    public String toString() {
        return "PageBean{" +
                "curPage=" + curPage +
                ", totalRecord=" + totalRecord +
                ", pageRecord=" + pageRecord +
                ", sort='" + orderBy + '\'' +
                '}';
    }

}
