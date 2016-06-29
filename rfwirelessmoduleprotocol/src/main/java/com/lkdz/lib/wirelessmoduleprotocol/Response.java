package com.lkdz.lib.wirelessmoduleprotocol;

import android.support.annotation.NonNull;

/**
 * Created by DELL on 2016/6/29.
 */
public class Response {
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

        if (data.length == 16 &&
                data[1] == 0x01 && data[2] == 0xDD && data[3] == 0x09 &&
                data[9] == 0x10 && data[14] == 0xF7 && data[15] == 0xC9) {
            return true;
        }

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
