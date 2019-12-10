package net.unsun.infrastructure.common.kit;

import net.unsun.infrastructure.common.constant.BaseCode;

import java.io.Serializable;

/**
 * 公共结果集返回视图（针对json接口）
 *
 * @author Tokey
 * @version 1.0.0
 * @since 2017/07/26 15:25
 */
public class ResultBean<T> implements Serializable {

    /**
     * 状态码：-1 参数校验错误；1 成功；2 失败 4 空数据; 5异常; 6未登录;
     */
    private int code = BaseCode.success.getCode();

    /**
     * 状态码：-1 参数校验错误；1 成功；2 失败 4 空数据; 5异常; 6未登录;
     */
    private String codeExplain = BaseCode.success.getCodeExplain();
    /**
     * 返回信息
     */
    private String message = null;
    /**
     * 请求成功/失败 标识
     */
    private Boolean success = true;
    /**
     * 扩展业务数据
     */
    private T data;

    public ResultBean() {
    }

    public ResultBean(BaseCode baseCode) {
        this.code = baseCode.getCode();
        this.codeExplain = baseCode.getCodeExplain();
    }

    /**
     * 创建结果集对象
     *
     * @return 当前对象
     */
    public static ResultBean create() {
        return new ResultBean();
    }

    /**
     * 返回成功结果集对象
     *
     * @return 当前对象
     */
    public static ResultBean success() {
        return new ResultBean(BaseCode.success);
    }

    /**
     * 返回失败结果集对象
     *
     * @return 当前对象
     */
    public static ResultBean fail() {
        return new ResultBean(BaseCode.fail).setSuccess(false);
    }


    /**
     * 返回空结果集对象
     *
     * @return 当前对象
     */
    public static ResultBean blank() {
        return new ResultBean(BaseCode.notFound).setSuccess(false);
    }

    /**
     * 返回异常结果集对象
     *
     * @return 当前对象
     */
    public static ResultBean exception() {
        return new ResultBean(BaseCode.exception).setSuccess(false);
    }

    /**
     * 返回返回登录结果集对象
     *
     * @return 当前对象
     */
    public static ResultBean notlogin() {
        return new ResultBean(BaseCode.notLogin).setSuccess(false);
    }


    public int getCode() {
        return code;
    }

    public ResultBean setCode(BaseCode baseCode) {
        this.code = baseCode.getCode();
        this.codeExplain = baseCode.getCodeExplain();
        return this;
    }


    public String getMessage() {
        return message;
    }

    public ResultBean setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public ResultBean setData(T data) {
        this.data = data;
        return this;
    }

    public Boolean getSuccess() {
        return success;
    }

    public ResultBean setSuccess(Boolean success) {
        this.success = success;
        return this;
    }


    @Override
    public String toString() {
        return "ResultBean{" +
                "code=" + code +
                ", codeExplain='" + codeExplain + '\'' +
                ", message='" + message + '\'' +
                ", success=" + success +
                ", data=" + data +
                '}';
    }
}