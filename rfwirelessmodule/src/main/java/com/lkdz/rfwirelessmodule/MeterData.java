package com.lkdz.rfwirelessmodule;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 无线智能燃气表数据。
 */
public class MeterData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String readingCount;        // 读数
    private float cellVoltage;          // 电池电压
    private int signalReq;              // 请求端信号强度
    private int signalRes;              // 应答端信号强度
    private ValveState valveState;      // 阀门状况
    private Calendar systemTime;        // 系统时间（读时间命令返回）
    private TimeSwitch timeSwitch;      // 唤醒时间（读时间命令返回）

    public MeterData() {
        readingCount = "";
        valveState = ValveState.UNKNOWN;
        systemTime = null;
        timeSwitch = null;
    }

    public String getReadingCount() {
        return readingCount;
    }

    public void setReadingCount(String readingCount) {
        this.readingCount = readingCount;
    }

    public float getCellVoltage() {
        return cellVoltage;
    }

    public void setCellVoltage(float cellVoltage) {
        this.cellVoltage = cellVoltage;
    }

    public int getSignalReq() {
        return signalReq;
    }

    public void setSignalReq(int signalReq) {
        this.signalReq = signalReq;
    }

    public int getSignalRes() {
        return signalRes;
    }

    public void setSignalRes(int signalRes) {
        this.signalRes = signalRes;
    }

    public ValveState getValveState() {
        return valveState;
    }

    public void setValveState(ValveState valveState) {
        this.valveState = valveState;
    }

    public Calendar getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(Calendar systemTime) {
        this.systemTime = systemTime;
    }

    public TimeSwitch getTimeSwitch() {
        return timeSwitch;
    }

    public void setTimeSwitch(TimeSwitch timeSwitch) {
        this.timeSwitch = timeSwitch;
    }

}
