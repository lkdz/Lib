package com.lkdz.rfwirelessmodule;

/**
 * Created by jiliang on 16/5/23.
 */
public enum ValveState {
    OPEN("开阀", 0x00),
    CLOSE("关阀", 0x01),
    UNKNOWN("未知", 0xFF);

    // 成员变量
    private String desc;
    private int code;

    // 构造方法
    private ValveState(String desc, int code) {
        this.desc = desc;
        this.code = code;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
