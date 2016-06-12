package com.lkdz.lib.rfwirelessmoduleprotocol;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 终端无线模块应答数据。
 * <p>通过Response.resolve方法将字节数组的解析成对应的数据对象。
 */
public class Response {
    /**
     * 表值。
     */
    public static class MeterValue {
        private String mCounter = "";
        private String mVoltage = "";
        private String mSignalRequest = "";
        private String mSignalResponse = "";
        private String mValveState = "";

        public String getCounter() {
            return mCounter;
        }

        public void setCounter(String counter) {
            mCounter = counter;
        }

        public String getVoltage() {
            return mVoltage;
        }

        public void setVoltage(String voltage) {
            mVoltage = voltage;
        }

        public String getSignalRequest() {
            return mSignalRequest;
        }

        public void setSignalRequest(String signalRequest) {
            mSignalRequest = signalRequest;
        }

        public String getSignalResponse() {
            return mSignalResponse;
        }

        public void setSignalResponse(String signalResponse) {
            mSignalResponse = signalResponse;
        }

        public String getValveState() {
            return mValveState;
        }

        public void setValveState(@ValveState String valveState) {
            mValveState = valveState;
        }


    }

    /**
     * 表时间。
     */
    public static class MeterTime {
        private String mWorkStartDay = "";
        private String mWorkStopDay = "";
        private String mWorkStartHour = "";
        private String mWorkStopHour = "";
        private String mSystemTime = "";

        public String getWorkStartDay() {
            return mWorkStartDay;
        }

        public void setWorkStartDay(String workStartDay) {
            mWorkStartDay = workStartDay;
        }

        public String getWorkStopDay() {
            return mWorkStopDay;
        }

        public void setWorkStopDay(String workStopDay) {
            mWorkStopDay = workStopDay;
        }

        public String getWorkStartHour() {
            return mWorkStartHour;
        }

        public void setWorkStartHour(String workStartHour) {
            mWorkStartHour = workStartHour;
        }

        public String getWorkStopHour() {
            return mWorkStopHour;
        }

        public void setWorkStopHour(String workStopHour) {
            mWorkStopHour = workStopHour;
        }

        public String getSystemTime() {
            return mSystemTime;
        }

        public void setSystemTime(String systemTime) {
            mSystemTime = systemTime;
        }
    }

    /** {@hide} */
    @StringDef({FlAG_OPEN_VALVE, FLAG_CLOSE_VALVE})
    @Retention(RetentionPolicy.CLASS)
    public @interface ValveState {}

    public static final String FlAG_OPEN_VALVE = "开阀";
    public static final String FLAG_CLOSE_VALVE = "关阀";

    /**
     * 解析应答数据
     * @param data 接收到的字节数组
     * @return MeterValue、MeterTime或Boolean类型的对象
     * @throws Exception 终端无线模块无任何应答将抛出异常
     */
    public static Object resolve(@NonNull byte[] data) throws Exception {
        if (data == null || data.length == 0) {
            throw new Exception("接收的数据为Null或Empty");
        }

        int[] arrayData = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            arrayData[i] = data[i] & 0xFF;
        }

        int venderForHezi = 0;  //0:龙凯 1：兆富

        //region 提取第一条完整协议的数据包
        int[] fullData = null;
        for (int i = 0; i < arrayData.length; i++) {
            if (arrayData.length - i < 5) {
                break;
            }
            // 帧头
            if (arrayData[i] == 0x18) {
                int frameLen = arrayData[i + 1] + 2; // 帧长度
                int dataLen = arrayData.length - i;  // 包长度
                if (frameLen > dataLen) {
                    continue;
                }
                // 命令字
                if (arrayData[i + 2] != 0x80 &&
                        arrayData[i + 2] != 0x81 &&
                        arrayData[i + 2] != 0x01 &&
                        arrayData[i + 2] != 0x02) {
                    continue;
                }
                // 长度
                if (arrayData[i + 2] == 0x80 &&
                        arrayData[i + 1] != 0x09 && arrayData[i + 1] != 0x0C) {
                    continue;
                }
                if (arrayData[i + 2] == 0x81 &&
                        arrayData[i + 1] != 0x0D) {
                    continue;
                }
                if (arrayData[i + 2] == 0x01 &&
                        arrayData[i + 1] != 0x03) {
                    continue;
                }
                if (arrayData[i + 2] == 0x02 &&
                        arrayData[i + 1] != 0x08) {
                    continue;
                }
                // 校验码
                if (getSum(arrayData, i + 1, frameLen - 2) != arrayData[frameLen - 1 + i]) {
                    continue;
                }
                fullData = new int[frameLen];
                System.arraycopy(arrayData, i, fullData, 0, frameLen);
                break;
            }
        }
        if (fullData == null) {
            // 检查是否是兆富盒子返回来的数据
            for (int i = 0; i < arrayData.length; i++) {
                if (arrayData.length - i < 14) {
                    break;
                }
                // 判断帧头
                if (arrayData[i] == 0x55 &&
                        arrayData[i + 1] == 0x72 &&
                        arrayData[i + 2] == 0xEE &&
                        arrayData[i + 3] == 0x0A) {
                    fullData = new int[14];
                    System.arraycopy(arrayData, i, fullData, 0, fullData.length);
                    venderForHezi = 1;
                    break;
                }
            }
        }

        if (fullData == null) {
            throw new Exception("没有发现符合通讯协议格式的数据");
        }
        //endregion

        //region 解析数据包并生成对象
        if (venderForHezi == 0) {
            switch (fullData[2]) {
                case 0x01:
                    switch (fullData[3]) {
                        case 0x03:
                            throw new Exception("读无线模块失败（读取超时或接收有误）");
                        case 0x04:
                            throw new Exception("发送的数据错误");
                        case 0x05:
                            return true;    //设置成功
                        case 0x06:
                            return false;   //设置失败
                        default:
                            break;
                    }
                case 0x02:
                    String voltage = String.format("%02X", fullData[3]);
                    String counter = String.format("%02X", fullData[4]) +
                            String.format("%02X", fullData[5]) +
                            String.format("%02X", fullData[6]);
                    String signalRes = Integer.toString(fullData[7]);
                    String signalReq = Integer.toString(fullData[8]);
                    MeterValue meterValue = new MeterValue();
                    meterValue.setCounter(counter);
                    meterValue.setVoltage(voltage);
                    meterValue.setSignalRequest(signalReq);
                    meterValue.setSignalResponse(signalRes);
                    return meterValue;
                case 0x80:
                    if (fullData[1] == 0x09) {
                        String counter1 = String.format("%02x", fullData[5]) +
                                String.format("%02x", fullData[6]) +
                                String.format("%02x", fullData[7]);
                        String voltage1 = String.format("%02x", fullData[8]);
                        MeterValue meterValue1 = new MeterValue();
                        meterValue1.setCounter(counter1);
                        meterValue1.setVoltage(voltage1);
                        return meterValue1;
                    } else if (fullData[1] == 0x0C) {
                        String signalReq1 = Integer.toString(fullData[4]);
                        String counter1 = String.format("%02x", fullData[5]) +
                                String.format("%02x", fullData[6]) +
                                String.format("%02x", fullData[7]);
                        String signalRes1 = Integer.toString(fullData[9]);
                        String voltage1 = String.format("%02x", fullData[11]);
                        String valveState = ((fullData[10] & 0x01) == 1) ? FLAG_CLOSE_VALVE : FlAG_OPEN_VALVE;
                        MeterValue meterValue1 = new MeterValue();
                        meterValue1.setCounter(counter1);
                        meterValue1.setVoltage(voltage1);
                        meterValue1.setSignalRequest(signalReq1);
                        meterValue1.setSignalResponse(signalRes1);
                        meterValue1.setValveState(valveState);
                        return meterValue1;
                    } else {
                        throw new Exception("没有发现符合通讯协议格式的数据");
                    }
                case 0x81:
                    String systemTime = Integer.toString(2000 + fullData[5]) + "/" +
                            Integer.toString(fullData[6]) + "/" +
                            Integer.toString(fullData[7]) + " " +
                            Integer.toString(fullData[8]) + ":" +
                            Integer.toString(fullData[9]) + ":0";
                    String workStartDay = Integer.toString(fullData[10]);
                    String workStopDay = Integer.toString(fullData[11]);
                    String workStartHour = Integer.toString(fullData[12]);
                    String workStopHour = Integer.toString(fullData[13]);
                    MeterTime meterTime = new MeterTime();
                    meterTime.setSystemTime(systemTime);
                    meterTime.setWorkStartDay(workStartDay);
                    meterTime.setWorkStopDay(workStopDay);
                    meterTime.setWorkStartHour(workStartHour);
                    meterTime.setWorkStopHour(workStopHour);
                    return meterTime;
                default:
                    break;
            }
        }
        else if (venderForHezi == 1) {
            switch (fullData[9]) {
                case 0x08:
                    throw new Exception("读取模块超时");
                case 0x40:  // 读数有错误?
                case 0x00:  // 读数正常
                    String counter = String.format("%02x", fullData[10]) +
                            String.format("%02x", fullData[11]) +
                            String.format("%02x", fullData[12]);
                    int nVoltage = (((fullData[13] - 0x30) * 5) / 10) + 0x16;
                    MeterValue meterValue = new MeterValue();
                    meterValue.setCounter(counter);
                    meterValue.setVoltage(Integer.toString(nVoltage));
                    return meterValue;
                default:
                    break;
            }
        }
        //endregion
        throw new Exception("没有发现符合通讯协议格式的数据");
    }

    private static int getSum(int[] array, int offset, int length) {
        int sum = 0;
        for (int i = offset; i < offset + length; i++) {
            sum += array[i];
        }
        return sum & 0xFF;
    }

}
