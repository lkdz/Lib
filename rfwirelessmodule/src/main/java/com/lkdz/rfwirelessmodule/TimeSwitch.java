package com.lkdz.rfwirelessmodule;

/**
 * 无线模块的开日、关日、开时、关时
 */
public class TimeSwitch {
    private int startDay;       // 开始日期 1~31
    private int stopDay;        // 停止日期 1~31
    private int startTime;      // 开始时间 0~23
    private int stopTime;       // 停止时间 0~23

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        if (startDay < 1 || startDay > 31) {
            this.startDay = 1;
        }
        else {
            this.startDay = startDay;
        }
    }

    public int getStopDay() {
        return stopDay;
    }

    public void setStopDay(int stopDay) {
        if (stopDay < 1 || stopDay > 31) {
            this.stopDay = 31;
        }
        else {
            this.stopDay = stopDay;
        }
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        if (startTime < 0 || startTime > 23) {
            this.startTime = 0;
        }
        else {
            this.startTime = startTime;
        }
    }

    public int getStopTime() {
        return stopTime;
    }

    public void setStopTime(int stopTime) {
        if (stopTime < 0 || stopTime > 23) {
            this.stopTime = 23;
        }
        else {
            this.stopTime = stopTime;
        }
    }
}
