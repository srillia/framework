package net.unsun.infrastructure.rpc.entity;

/**
 * RpcMode
 *
 * @author toby
 */
public enum RpcMode {

    // 客户端
    RPC_CLIENT(0, "客户端"),
    // 服务端
    RPC_SERVER(1, "服务端");

    private int mode;
    private String name;

    RpcMode(int mode, String name) {
        this.mode = mode;
        this.name = name;
    }

    public int getMode() {
        return mode;
    }

    public String getName() {
        return name;
    }

    public static RpcMode getRpcMode(Integer mode) {
        if (mode == null) {
            return RPC_SERVER;
        }
        for (RpcMode e : RpcMode.values()) {
            if (e.mode == mode) {
                return e;
            }
        }
        return RPC_SERVER;
    }
}
