package com.lkdz.rfwirelessmodule;

/**
 * Created by DELL on 2016/5/24.
 */
public class CommResult {
    private boolean isSuccess;
    private String errMsg;
    private MeterData meterData;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public MeterData getMeterData() {
        return meterData;
    }

    public void setMeterData(MeterData meterData) {
        this.meterData = meterData;
    }
}
