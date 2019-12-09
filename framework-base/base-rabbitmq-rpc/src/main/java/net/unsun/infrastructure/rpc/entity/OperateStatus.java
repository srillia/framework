package net.unsun.infrastructure.rpc.entity;

/**
 * 业务执行状态码
 *
 * @author toby
 */
public enum OperateStatus {

    // 调用成功
    SUCCESS(1, "Success"),
    // 调用失败
    FAILURE(0, "Failure");

    private int status;
    private String message;

    OperateStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public static OperateStatus getOperateStatus(Integer status) {
        if (status == null) {
            return FAILURE;
        }
        for (OperateStatus e : OperateStatus.values()) {
            if (e.status == status) {
                return e;
            }
        }
        return FAILURE;
    }
}
