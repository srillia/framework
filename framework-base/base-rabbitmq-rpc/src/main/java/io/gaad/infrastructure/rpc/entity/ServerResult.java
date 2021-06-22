package io.gaad.infrastructure.rpc.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * ServerResult
 *
 * @author toby
 */
public class ServerResult<T> {

    private OperateStatus operateStatus;
    private String message;
    private T result;
    private int errorCode;

    public ServerResult(OperateStatus operateStatus, String message, T result, int errorCode) {
        this.operateStatus = operateStatus;
        this.message = message;
        this.result = result;
        this.errorCode = errorCode;
    }

    public static ServerResult build(OperateStatus operateStatus) {
        return new ServerResult(operateStatus, operateStatus.getMessage(), null, 0);
    }

    public ServerResult message(String message) {
        this.message = message;
        return this;
    }

    public ServerResult result(T result) {
        if (result != null) {
            this.result = result;
        }
        return this;
    }

    public ServerResult errorCode(int errorCode) {
        if (this.operateStatus == OperateStatus.FAILURE) {
            this.errorCode = errorCode;
        }
        return this;
    }

    public OperateStatus getOperateStatus() {
        return operateStatus;
    }

    public String getMessage() {
        return message;
    }

    public T getResult() {
        return result;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        result.put("status", this.operateStatus.getStatus());
        result.put("message", this.message);
        result.put("result", this.result == null ? new JSONObject() : this.result);
        if (this.errorCode != 0) {
            result.put("errorCode", this.errorCode);
        }
        return result.toJSONString();
    }

}