package io.gaad.infrastructure.common.kit;

import java.io.Serializable;

/**
 * 日志信息规范Javabean
 *
 * @author Tokey
 * @version 1.0.0
 * @since 2017/10/26 10:36
 */
public final class LogInfoBean implements Serializable {

    /**
     * 日志来源（此日志来自哪一个应用，包信息等）
     */
    private Object source = null;
    /**
     * 日志类型（fatal，error，warn，info，debug）
     */
    private String type = "error";
    /**
     * 日志编码
     */
    private String code = null;
    /**
     * 消息内容
     */
    private String message;
    /**
     * 异常堆栈消息(为减少堆栈消息过大，可以尽量传递最原始的对象，例如调用：Throwables.getRootCause(cause) )
     */
    private Throwable cause;
    /**
     * 当前操作者
     */
    private String operator = "system";
    /**
     * 日志发生时间戳
     */
    private Long createAt = System.currentTimeMillis();
    /**
     * 是否发送通知
     */
    private Boolean isNotify = true;
    /**
     * 通知账号（邮箱账号）
     */
    private String notificationAccount;

    public LogInfoBean() {
    }

    public static LogInfoBean create() {
        return new LogInfoBean();
    }

    public Object getSource() {
        return source;
    }

    public LogInfoBean setSource(Object source) {
        this.source = source;
        return this;
    }

    public String getType() {
        return type;
    }

    public LogInfoBean setType(String type) {
        this.type = type;
        return this;
    }

    public String getCode() {
        return code;
    }

    public LogInfoBean setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public LogInfoBean setMessage(String message) {
        this.message = message;
        return this;
    }

    public Throwable getCause() {
        return cause;
    }

    public LogInfoBean setCause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public String getOperator() {
        return operator;
    }

    public LogInfoBean setOperator(String operator) {
        this.operator = operator;
        return this;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public LogInfoBean setCreateAt(Long createAt) {
        this.createAt = createAt;
        return this;
    }

    public Boolean getNotify() {
        return isNotify;
    }

    public LogInfoBean setNotify(Boolean notify) {
        isNotify = notify;
        return this;
    }

    public String getNotificationAccount() {
        return notificationAccount;
    }

    public LogInfoBean setNotificationAccount(String notificationAccount) {
        this.notificationAccount = notificationAccount;
        return this;
    }

    @Override
    public String toString() {
        return "LogInfoBean{" +
                "source=" + source +
                ", type='" + type + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", cause=" + cause +
                ", operator='" + operator + '\'' +
                ", createAt=" + createAt +
                ", isNotify=" + isNotify +
                ", notificationAccount='" + notificationAccount + '\'' +
                '}';
    }

}
