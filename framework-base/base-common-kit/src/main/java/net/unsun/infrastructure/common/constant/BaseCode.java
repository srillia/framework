package net.unsun.infrastructure.common.constant;

import java.io.Serializable;

/**
 * wtoip-framework
 *
 * @author Tokey
 * @version 1.0.0
 * @since 2017/09/25 16:27
 */
public enum BaseCode implements Serializable {

    illegalArgument(-1,"参数校验失败"),
    success(1,"操作成功"),
    fail(2,"操作失败"),
    systemBusy(3,"系统繁忙"),
    notFound(4,"未找到对应结果"),
    exception(5,"发生异常"),
    notLogin(6,"您还未登录"),
    authFeiled(7,"认证失败"),
    notPermited(8,"没有授权");

    int code;

    String codeExplain;

    BaseCode(int code,String codeExplain) {
        this.code = code;
        this.codeExplain = codeExplain;
    }


    public int getCode() {
        return code;
    }

    public String getCodeExplainByCode(int code) {
        for (BaseCode baseCode : values()) {
            if (baseCode.getCode() == code) {
                return baseCode.codeExplain;
            }
        }
        return null;
    }


    public String getCodeExplain() {
        return codeExplain;
    }

    @Override
    public String toString() {
        return "BaseCode{" +
                "code=" + code +
                ", codeExplain='" + codeExplain + '\'' +
                '}';
    }
}
