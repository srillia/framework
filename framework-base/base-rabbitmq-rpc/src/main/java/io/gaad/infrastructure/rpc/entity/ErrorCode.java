package io.gaad.infrastructure.rpc.entity;

/**
 * HTTP请求错误码
 *
 * @author toby
 */
public enum ErrorCode {

    // 请求参数不正确
    PARAMS_NOT_VALID(400, "Params Not Valid"),
    // 权限校验失败(未登录或者登录已过期)
    AUTHORIZED_FAILED(401, "Authorized Failed"),
    // 禁止访问
    FORBIDDEN(403, "Forbidden"),
    // 找不到
    NOT_FOUND(404, "Not Found"),
    // Method not allowed
    METHOD_NOT_ALLOWED(405, "Method not allowed"),
    // 服务器内部错误
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    // 服务不可用
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    // 请求超时(服务器负载过高，未能及时处理请求)
    GATEWAY_TIMEOUT(504, "Gateway Timeout");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorCode getErrorCode(Integer code) {
        if (code == null) {
            return INTERNAL_SERVER_ERROR;
        }
        for (ErrorCode e : ErrorCode.values()) {
            if (e.code == code) {
                return e;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }

}
