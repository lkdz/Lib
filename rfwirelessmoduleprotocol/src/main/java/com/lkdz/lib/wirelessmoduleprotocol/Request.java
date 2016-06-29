package com.lkdz.lib.wirelessmoduleprotocol;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by DELL on 2016/6/29.
 */
public class Request {
    @IntDef({EDIT_ID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Operation {
    }

    /**
     * 设置表号
     */
    public static final int EDIT_ID = 6;


    private void checkId(String id) throws IllegalArgumentException {
        if (TextUtils.isEmpty(id) || id.length() > 12) {
            throw new IllegalArgumentException("表号不正确");
        }
    }

    private byte[] convertId(String id) {
        StringBuilder idPaddingLeftWithSpace = new StringBuilder();
        for (int i = 0; i < 12 - id.length(); i++) {
            idPaddingLeftWithSpace.append(" ");
        }
        idPaddingLeftWithSpace.append(id);

        return idPaddingLeftWithSpace.toString().getBytes();
    }

    private static byte getSum(byte[] array, int offset, int length) {
        int sum = 0;
        for (int i = offset; i < offset + length; i++) {
            sum += (int) array[i];
        }
        return (byte) (sum & 0xFF);
    }


    private Request(Builder builder) {
    }

    /**
     * 对终端无线模块的请求指令的构造器。
     * 使请求指令中参数的设置更加清晰了然。
     */
    public static class Builder {
        public Builder() {
        }

        /**
         * 构建请求指令集合
         * @return Request实例
         */
        public Request build() {
            return new Request(this);
        }
    }

    /**
     *
     * @param operation 请求指令类型
     * @param id 无线表号
     * @return 请求指令的字节数组形式
     * @throws IllegalArgumentException 无效参数异常
     */
    public byte[] create(@Operation int operation, @NonNull String id) throws IllegalArgumentException {
        checkId(id);

        byte[] array = null;
        switch (operation) {
            case EDIT_ID:
                //region 设置表号
                array = new byte[19];
                array[0] = (byte) 0x12;
                array[1] = (byte) 0x02;
                array[2] = (byte) 0x99;
                array[3] = (byte) 0x99;
                array[4] = (byte) 0x99;
                array[5] = (byte) 0x99;
                System.arraycopy(convertId(id), 0, array, 6, 12);
                array[18] = getSum(array, 1, 23);
                //endregion
                break;
            default:
                break;
        }
        return array;
    }

}
