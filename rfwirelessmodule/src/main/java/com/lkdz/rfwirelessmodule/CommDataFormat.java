package com.lkdz.rfwirelessmodule;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;

/**
 * 无线模块通讯数据格式.
 * <p>通过setFrequency或setCheckSignal来设置495带信号、495不带信号、470带信号、470不带信号这几种类型的无线模块.</p>
 */
public class CommDataFormat {
    private CommDataFormat() {
        frequency = Frequency.M495;
        checkSignal = true;
    }

    private static final CommDataFormat commDataFormat = new CommDataFormat();

    public static CommDataFormat getInstance() {
        return commDataFormat;
    }

    private Frequency frequency;

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    private boolean checkSignal;

    public boolean isCheckSignal() {
        return checkSignal;
    }

    public void setCheckSignal(boolean checkSignal) {
        this.checkSignal = checkSignal;
    }

    // 抄表命令

    /**
     * 获取抄表命令。
     *
     * @param meterId 无线表号（限8位或10位长度的数字，且470模块的的最大表号不能大于4294967295）
     * @return
     */
    @Nullable
    public byte[] getArrayForReading(String meterId) {
        return getArrayForReading(meterId, Calendar.getInstance(), getTimeSwitch(frequency));
    }

    @Nullable
    public byte[] getArrayForReading(String meterId, Calendar systemTime) {
        return getArrayForReading(meterId, systemTime, getTimeSwitch(frequency));
    }

    @Nullable
    public byte[] getArrayForReading(String meterId, TimeSwitch timeSwitch) {
        return getArrayForReading(meterId, Calendar.getInstance(), timeSwitch);
    }

    @Nullable
    public byte[] getArrayForReading(String meterId, Calendar systemTime, TimeSwitch timeSwitch) {
        if (getBytesOfMeterId(meterId) == null) {
            return null;
        }
        // 495、470、以及470带信号都是同一格式，并需要设置系统时间和定时唤醒；
        // 495带信号是单独一条格式（龙凯内部命令），且只需表号即可；
        if (!(checkSignal && frequency == Frequency.M495) &&
                (getBytesOfSystemTime(systemTime) == null || getBytesOfTimeSwitch(timeSwitch) == null)) {
            return null;
        }

        byte[] array;
        if (checkSignal && frequency == Frequency.M495) {
            array = new byte[11];
            array[0] = (byte) 0x18;
            array[1] = (byte) 0x09;
            array[2] = (byte) 0xFD;
            array[3] = (byte) 0x00;
            array[4] = (byte) 0x05;
            System.arraycopy(getBytesOfMeterId(meterId), 0, array, 5, 5);
            array[10] = getSum(array, 1, 9);
        } else {
            array = new byte[20];
            array[0] = (byte) 0x18;
            array[1] = (byte) 0x12;
            array[2] = (byte) 0x02;
            array[3] = (byte) 0x00;
            array[4] = (byte) 0x05;
            System.arraycopy(getBytesOfMeterId(meterId), 0, array, 5, 5);
            System.arraycopy(getBytesOfSystemTime(systemTime), 0, array, 10, 5);
            System.arraycopy(getBytesOfTimeSwitch(timeSwitch), 0, array, 15, 4);
            array[19] = getSum(array, 1, 18);
        }
        return array;
    }

    // 读时命令
    public byte[] getArrayForTiming(String meterId) {
        if (getBytesOfMeterId(meterId) == null) {
            return null;
        }

        byte[] array;
        array = new byte[20];
        array[0] = (byte) 0x18;
        array[1] = (byte) 0x12;
        array[2] = (byte) 0x05;
        array[3] = (byte) 0x00;
        array[4] = (byte) 0x05;
        System.arraycopy(getBytesOfMeterId(meterId), 0, array, 5, 5);
        array[10] = (byte) 0x00;
        array[11] = (byte) 0x00;
        array[12] = (byte) 0x00;
        array[13] = (byte) 0x00;
        array[14] = (byte) 0x00;
        array[15] = (byte) 0x00;
        array[16] = (byte) 0x00;
        array[17] = (byte) 0x00;
        array[18] = (byte) 0x00;
        array[19] = getSum(array, 1, 18);
        return array;
    }

    // 开阀命令
    public byte[] getArrayForOpeningValve(String meterId) {
        if (getBytesOfMeterId(meterId) == null) {
            return null;
        }

        byte[] array;
        array = new byte[20];
        array[0] = (byte) 0x18;
        array[1] = (byte) 0x12;
        array[2] = (byte) 0x07;
        array[3] = (byte) 0x00;
        array[4] = (byte) 0x05;
        System.arraycopy(getBytesOfMeterId(meterId), 0, array, 5, 5);
        System.arraycopy(getBytesOfSystemTime(Calendar.getInstance()), 0, array, 10, 5);
        System.arraycopy(getBytesOfTimeSwitch(getTimeSwitch(frequency)), 0, array, 15, 4);
        array[19] = getSum(array, 1, 18);
        return array;
    }

    // 关阀命令
    public byte[] getArrayForClosingValve(String meterId) {
        if (getBytesOfMeterId(meterId) == null) {
            return null;
        }

        byte[] array;
        array = new byte[20];
        array[0] = (byte) 0x18;
        array[1] = (byte) 0x12;
        array[2] = (byte) 0x08;
        array[3] = (byte) 0x00;
        array[4] = (byte) 0x05;
        System.arraycopy(getBytesOfMeterId(meterId), 0, array, 5, 5);
        System.arraycopy(getBytesOfSystemTime(Calendar.getInstance()), 0, array, 10, 5);
        System.arraycopy(getBytesOfTimeSwitch(getTimeSwitch(frequency)), 0, array, 15, 4);
        array[19] = getSum(array, 1, 18);
        return array;
    }

    // 设置表号命令
    public byte[] getArrayForEditingMeterId(String meterId) {
        if (getBytesOfMeterId(meterId) == null) {
            return null;
        }

        byte[] array;
        array = new byte[25];
        array[0] = (byte) 0x18;
        array[1] = (byte) 0x17;
        array[2] = (byte) 0x04;
        array[3] = (byte) 0x00;
        array[4] = (byte) 0x05;
        array[5] = (byte) 0x99;
        array[6] = (byte) 0x99;
        array[7] = (byte) 0x99;
        array[8] = (byte) 0x99;
        array[9] = frequency == Frequency.M495 ? (byte) 0x99 : (byte) 0x00;
        System.arraycopy(getBytesOfMeterId(meterId), 0, array, 10, 5);
        System.arraycopy(getBytesOfSystemTime(Calendar.getInstance()), 0, array, 15, 5);
        System.arraycopy(getBytesOfTimeSwitch(getTimeSwitch(frequency)), 0, array, 20, 4);
        array[24] = getSum(array, 1, 23);
        return array;
    }


    @Nullable

    private byte[] getBytesOfMeterId(String meterId) {
//        上海全部495，外地全部470。
//        470全部为10位表号需要转换为十六进制，最高不超过4个字节即FFFFFFFF
//        495又分为8、10位表号。10位表号就按照BCD码表示共5个字节。8位表号前面加'24'后按照10位表号方式处理。
//        PS：上海的条形码各地区不相同，有的前面加个3（金山、崇明），有的后面加校验码（大众、浦销）。

        if (meterId == null ||
                !meterId.matches("^\\d+$") ||
                (meterId.length() != 8 && meterId.length() != 10) ||
                (meterId.length() == 10 && frequency == Frequency.M470 && Long.parseLong(meterId) > 0xFFFFFFFFL)) {
            return null;
        }

        byte[] bytes = new byte[5];
        if (meterId.length() == 8) {
            bytes[0] = (byte) 0x24;
            bytes[1] = (byte) (Integer.parseInt(meterId.substring(0, 2), 16) & 0xFF);
            bytes[2] = (byte) (Integer.parseInt(meterId.substring(2, 4), 16) & 0xFF);
            bytes[3] = (byte) (Integer.parseInt(meterId.substring(4, 6), 16) & 0xFF);
            bytes[4] = (byte) (Integer.parseInt(meterId.substring(6, 8), 16) & 0xFF);
        } else if (meterId.length() == 10) {
            if (frequency == Frequency.M470) {
                Long idLong = Long.parseLong(meterId);
                bytes[0] = (byte) ((idLong >>> 24) & 0xFF);     // 最高位,无符号右移。
                bytes[1] = (byte) ((idLong >> 16) & 0xFF);      // 次高位
                bytes[2] = (byte) ((idLong >> 8) & 0xFF);       // 次低位
                bytes[3] = (byte) (idLong & 0xFF);              // 最低位
                bytes[4] = (byte) 0x00;
            } else if (this.frequency == Frequency.M495) {
                bytes[0] = (byte) (Integer.parseInt(meterId.substring(0, 2), 16) & 0xFF);
                bytes[1] = (byte) (Integer.parseInt(meterId.substring(2, 4), 16) & 0xFF);
                bytes[2] = (byte) (Integer.parseInt(meterId.substring(4, 6), 16) & 0xFF);
                bytes[3] = (byte) (Integer.parseInt(meterId.substring(6, 8), 16) & 0xFF);
                bytes[4] = (byte) (Integer.parseInt(meterId.substring(8, 10), 16) & 0xFF);
            } else {
                return null;
            }
        }
        return bytes;
    }

    @Nullable
    private byte[] getBytesOfSystemTime(Calendar systemTime) {
        byte[] bytes = new byte[5];
        bytes[0] = (byte) (systemTime.get(Calendar.YEAR) - 2000);
        bytes[1] = (byte) (systemTime.get(Calendar.MONTH) + 1);
        bytes[2] = (byte) systemTime.get(Calendar.DATE);
        bytes[3] = (byte) systemTime.get(Calendar.HOUR_OF_DAY);
        bytes[4] = (byte) systemTime.get(Calendar.MINUTE);
        return bytes;
    }

    @Nullable
    private byte[] getBytesOfTimeSwitch(TimeSwitch timeSwitch) {
        if (timeSwitch == null) {
            return null;
        }

        byte[] bytes = new byte[4];
        bytes[0] = (byte) (timeSwitch.getStartDay() & 0xFF);
        bytes[1] = (byte) (timeSwitch.getStopDay() & 0xFF);
        bytes[2] = (byte) (timeSwitch.getStartTime() & 0xFF);
        bytes[3] = (byte) (timeSwitch.getStopTime() & 0xFF);
        return bytes;
    }

    @NonNull
    private byte getSum(byte[] array, int offset, int length) {
        int sum = 0;
        for (int i = offset; i < offset + length; i++) {
            sum += (int) array[i];
        }
        return (byte) (sum & 0xFF);
    }

    @NonNull
    private int getSum(int[] array, int offset, int length) {
        int sum = 0;
        for (int i = offset; i < offset + length; i++) {
            sum += array[i];
        }
        return sum & 0xFF;
    }

    @NonNull
    private TimeSwitch getTimeSwitch(Frequency frequency) {
        TimeSwitch timeSwitch = new TimeSwitch();

        switch (frequency) {
            case M470:
                timeSwitch.setStartDay(1);
                timeSwitch.setStopDay(31);
                timeSwitch.setStartTime(0);
                timeSwitch.setStopTime(23);
                break;
            case M495:
                timeSwitch.setStartDay(1);
                timeSwitch.setStopDay(31);
                timeSwitch.setStartTime(6);
                timeSwitch.setStopTime(20);
                break;
            default:
                timeSwitch.setStartDay(1);
                timeSwitch.setStopDay(31);
                timeSwitch.setStartTime(0);
                timeSwitch.setStopTime(23);
                break;
        }

        return timeSwitch;
    }

    /**
     * 分析接收数据并返回其中的燃气表数据.
     *
     * @param recvData 从蓝牙接收到的数据.
     * @return
     */
    @NonNull
    public CommResult getMeterData(byte[] recvData) {
        CommResult commResult = new CommResult();
        commResult.setSuccess(false);
        commResult.setErrMsg("没有发现符合通讯协议的数据");

        if (recvData == null || recvData.length == 0) {
            commResult.setErrMsg("接收的数据为Null或Empty");
            return commResult;
        }

        int[] recvDataAsIntArray = new int[recvData.length];
        for (int i = 0; i < recvData.length; i++) {
            recvDataAsIntArray[i] = recvData[i] & 0xFF;
        }

        // 提取第一条完整协议的数据包
        int[] fullData = null;
        for (int i = 0; i < recvDataAsIntArray.length; i++) {
            if (recvDataAsIntArray.length - i < 5) {
                return commResult;
            }
            // 帧头
            if (recvDataAsIntArray[i] == 0x18) {
                int frameLen = recvDataAsIntArray[i + 1] + 2; // 帧长度
                int dataLen = recvDataAsIntArray.length - i;  // 包长度
                if (frameLen > dataLen) {
                    continue;
                }
                // 命令字
                if (recvDataAsIntArray[i + 2] != 0x80 &&
                        recvDataAsIntArray[i + 2] != 0x81 &&
                        recvDataAsIntArray[i + 2] != 0x01 &&
                        recvDataAsIntArray[i + 2] != 0x02) {
                    continue;
                }
                // 长度
                if (recvDataAsIntArray[i + 2] == 0x80 &&
                        recvDataAsIntArray[i + 1] != 0x09 && recvDataAsIntArray[i + 1] != 0x0C) {
                    continue;
                }
                if (recvDataAsIntArray[i + 2] == 0x81 &&
                        recvDataAsIntArray[i + 1] != 0x0D) {
                    continue;
                }
                if (recvDataAsIntArray[i + 2] == 0x01 &&
                        recvDataAsIntArray[i + 1] != 0x03) {
                    continue;
                }
                if (recvDataAsIntArray[i + 2] == 0x02 &&
                        recvDataAsIntArray[i + 1] != 0x08) {
                    continue;
                }
                // 校验码
                if (getSum(recvDataAsIntArray, i + 1, frameLen - 2) != recvDataAsIntArray[frameLen - 1 + i]) {
                    continue;
                }
                fullData = new int[frameLen];
                System.arraycopy(recvDataAsIntArray, i, fullData, 0, frameLen);
                break;
            }
        }

        String strCellVoltage;
        String strReadingCount;
        int signalRes;
        int signalReq;
        ValveState valveState;
        TimeSwitch timeSwitch;
        Calendar calendar;
        MeterData meterData;

        switch (fullData[2]) {
            case 0x01:
                switch (fullData[3]) {
                    case 0x03:
                        commResult.setErrMsg("读无线模块失败（读取超时或接收有误）");
                        break;
                    case 0x04:
                        commResult.setErrMsg("发送的数据错误");
                        break;
                    case 0x05:
                        commResult.setSuccess(true);
                        break;
                    case 0x06:
                        commResult.setErrMsg("修改表号失败");
                        break;
                    default:
                        break;
                }
                break;
            case 0x02:
                strCellVoltage = String.format("%02X", fullData[3]);
                strReadingCount = String.format("%02X", fullData[4]) +
                        String.format("%02X", fullData[5]) +
                        String.format("%02X", fullData[6]);
                signalRes = fullData[7];
                signalReq = fullData[8];
                meterData = new MeterData();
                meterData.setReadingCount(strReadingCount);
                meterData.setCellVoltage(strCellVoltage.matches("^\\d+$") ? Integer.parseInt(strCellVoltage) / 10.0F : 0);
                meterData.setSignalReq(signalReq);
                meterData.setSignalRes(signalRes);
                commResult.setSuccess(true);
                commResult.setMeterData(meterData);
                break;
            case 0x80:
                if (fullData[1] == 0x09) {
                    strCellVoltage = String.format("%02x", fullData[8]);
                    strReadingCount = String.format("%02x", fullData[5]) +
                            String.format("%02x", fullData[6]) +
                            String.format("%02x", fullData[7]);
                    meterData = new MeterData();
                    meterData.setReadingCount(strReadingCount);
                    meterData.setCellVoltage(strCellVoltage.matches("^\\d+$") ? Integer.parseInt(strCellVoltage) / 10.0F : 0);
                    commResult.setSuccess(true);
                    commResult.setMeterData(meterData);
                } else if (fullData[1] == 0x0C) {
                    strCellVoltage = String.format("%02x", fullData[11]);
                    strReadingCount = String.format("%02x", fullData[5]) +
                            String.format("%02x", fullData[6]) +
                            String.format("%02x", fullData[7]);
                    signalReq = fullData[4];
                    signalRes = fullData[9];
                    valveState = ((fullData[10] & 0x01) == 1) ? ValveState.CLOSE : ValveState.OPEN;
                    meterData = new MeterData();
                    meterData.setReadingCount(strReadingCount);
                    meterData.setCellVoltage(strCellVoltage.matches("^\\d+$") ? Integer.parseInt(strCellVoltage) / 10.0F : 0);
                    meterData.setSignalReq(signalReq);
                    meterData.setSignalRes(signalRes);
                    meterData.setValveState(valveState);
                    commResult.setSuccess(true);
                    commResult.setMeterData(meterData);
                }
                break;
            case 0x81:
                calendar = Calendar.getInstance();
                calendar.set(2000 + fullData[5], fullData[6] - 1, fullData[7], fullData[8], fullData[9]);
                timeSwitch = new TimeSwitch();
                timeSwitch.setStartDay(fullData[10]);
                timeSwitch.setStopDay(fullData[11]);
                timeSwitch.setStartTime(fullData[12]);
                timeSwitch.setStopTime(fullData[13]);
                meterData = new MeterData();
                meterData.setSystemTime(calendar);
                meterData.setTimeSwitch(timeSwitch);
                commResult.setSuccess(true);
                commResult.setMeterData(meterData);
                break;
            default:
                break;
        }

        return commResult;
    }
}
