package net.unsun.infrastructure.rpc.entity;

/**
 * 调用类型
 *
 * @author toby
 */
public enum RpcType {

    // 同步
    SYNC(0, "SYNC"),
    // 异步
    ASYNC(1, "ASYNC");

    private int type;
    private String name;

    RpcType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static RpcType getRpcType(Integer type) {
        if (type == null) {
            return SYNC;
        }
        for (RpcType e : RpcType.values()) {
            if (e.type == type) {
                return e;
            }
        }
        return SYNC;
    }
}
