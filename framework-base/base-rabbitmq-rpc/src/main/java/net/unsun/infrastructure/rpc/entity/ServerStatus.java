package net.unsun.infrastructure.rpc.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * rpc调用状态码
 *
 * @author toby
 */
public enum ServerStatus {

    // 调用成功
    SUCCESS(1, "Call Success"),
    // 调用失败
    FAILURE(0, "Call Failure"),
    // 调用不存在
    NOT_EXIST(-1, "Service Not Exist"),
    // 调用超时, 服务不可用
    UNAVAILABLE(-2, "Service Unavailable");

    private int status;
    private String message;

    ServerStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public static ServerStatus getServerStatus(Integer status) {
        if (status == null) {
            return FAILURE;
        }
        for (ServerStatus e : ServerStatus.values()) {
            if (e.status == status) {
                return e;
            }
        }
        return FAILURE;
    }

    @Override
    public String toString() {
        JSONObject str = new JSONObject();
        str.put("status", this.status);
        str.put("message", this.message);
        return str.toJSONString();
    }
}
