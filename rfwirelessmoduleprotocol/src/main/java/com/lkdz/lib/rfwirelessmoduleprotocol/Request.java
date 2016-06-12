package com.lkdz.lib.rfwirelessmoduleprotocol;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

/**
 * 对终端无线模块的请求指令。
 * <p>使用Request.Builder类进行构造，然后使用Request.create方法生成符合通讯协议的字节数组。
 * <p>举例:
 * <pre class="prettyprint">
 * Request request = new Request.Builder(FREQUENCY_495)
 *         .setWorkStartDay(1)
 *         .setWorkStopDay(31)
 *         .setWorkStartHour(0)
 *         .setWorkStopHour(23)
 *         .setSleepDurationSecond(3)
 *         .setListenDurationMillisecond(10)
 *         .setBase(0)
 *         .build();
 * byte[] array = request.create(READ_METER_WITH_SIGNAL, "0616000001");
 * </pre>
 */
public class Request {
    @IntDef({READ_VALUE_WITH_SIGNAL, READ_VALUE, READ_TIME,
            OPEN_VALVE, CLOSE_VALVE,
            EDIT_ID, EDIT_BASE, EDIT_TIME,
            READ_VALUE_FROM_ZAOFU})
    @Retention(RetentionPolicy.CLASS)
    public @interface Operation {
    }

    /**
     * 读值（返回数据：计数器读数、电压值、信号值）
     */
    public static final int READ_VALUE_WITH_SIGNAL = 1;
    /**
     * 读值（返回数据：计数器读数、电压值）
     */
    public static final int READ_VALUE = 2;
    /**
     * 读时（返回数据：系统时间、开关日时）
     */
    public static final int READ_TIME = 3;
    /**
     * 开阀
     */
    public static final int OPEN_VALVE = 4;
    /**
     * 关阀
     */
    public static final int CLOSE_VALVE = 5;
    /**
     * 设置表号
     */
    public static final int EDIT_ID = 6;
    /**
     * 设置底数
     */
    public static final int EDIT_BASE = 7;
    /**
     * 设置时间
     */
    public static final int EDIT_TIME = 8;
    /**
     * 读值（使用兆富盒子）
     */
    public static final int READ_VALUE_FROM_ZAOFU = 102;

    @StringDef({FREQUENCY_470, FREQUENCY_495})
    @Retention(RetentionPolicy.CLASS)
    public @interface Frequency {
    }

    public static final String FREQUENCY_470 = "470";
    public static final String FREQUENCY_495 = "495";

    private static final int FREQUENCY_470_WORK_START_DAY_DEFAULT = 1;
    private static final int FREQUENCY_470_WORK_STOP_DAY_DEFAULT = 31;
    private static final int FREQUENCY_470_WORK_START_HOUR_DEFAULT = 0;
    private static final int FREQUENCY_470_WORK_STOP_HOUR_DEFAULT = 23;
    private static final int FREQUENCY_495_WORK_START_DAY_DEFAULT = 1;
    private static final int FREQUENCY_495_WORK_STOP_DAY_DEFAULT = 31;
    private static final int FREQUENCY_495_WORK_START_HOUR_DEFAULT = 6;
    private static final int FREQUENCY_495_WORK_STOP_HOUR_DEFAULT = 20;

    private static final int FREQUENCY_470_SLEEP_DURATION_SECOND_DEFAULT = 3;
    private static final int FREQUENCY_470_LISTEN_DURATION_MILLISECOND_DEFAULT = 10;
    private static final int FREQUENCY_495_SLEEP_DURATION_SECOND_DEFAULT = 3;
    private static final int FREQUENCY_495_LISTEN_DURATION_MILLISECOND_DEFAULT = 10;

    private static final String FREQUENCY_470_FACTORY_ID = "2576980377";
    private static final String FREQUENCY_495_FACTORY_ID = "9999999999";

    private static final double BASE_DEFAULT = 0;

    private String mFrequency;
    private int mWorkStartDay;
    private int mWorkStopDay;
    private int mWorkStartHour;
    private int mWorkStopHour;
    private int mSleepDurationSecond;
    private int mListenDurationMillisecond;
    private double mBase;

    public String getFrequency() {
        return mFrequency;
    }

    public void setFrequency(@Frequency String frequency) {
        mFrequency = frequency;
    }

    public int getWorkStartDay() {
        return mWorkStartDay;
    }

    public void setWorkStartDay(int workStartDay) {
        mWorkStartDay = workStartDay;
    }

    public int getWorkStopDay() {
        return mWorkStopDay;
    }

    public void setWorkStopDay(int workStopDay) {
        mWorkStopDay = workStopDay;
    }

    public int getWorkStartHour() {
        return mWorkStartHour;
    }

    public void setWorkStartHour(int workStartHour) {
        mWorkStartHour = workStartHour;
    }

    public int getWorkStopHour() {
        return mWorkStopHour;
    }

    public void setWorkStopHour(int workStopHour) {
        mWorkStopHour = workStopHour;
    }

    public int getSleepDurationSecond() {
        return mSleepDurationSecond;
    }

    public void setSleepDurationSecond(int sleepDurationSecond) {
        mSleepDurationSecond = sleepDurationSecond;
    }

    public int getListenDurationMillisecond() {
        return mListenDurationMillisecond;
    }

    public void setListenDurationMillisecond(int listenDurationMillisecond) {
        mListenDurationMillisecond = listenDurationMillisecond;
    }

    public double getBase() {
        return mBase;
    }

    public void setBase(double base) {
        mBase = base;
    }


    private void checkOperation(int operation) throws IllegalArgumentException {
        if (operation == READ_VALUE_FROM_ZAOFU && mFrequency == FREQUENCY_470)
            throw new IllegalArgumentException("不支持使用兆富盒子对470读表");
    }

    private void checkId(String id) throws IllegalArgumentException {
        // 470表号十六进制表示，4个字节。
        // 495表号BCD码表示，5个字节。

        if (id == null ||
                !id.matches("^\\d{10}$") ||
                (mFrequency == FREQUENCY_470 && Long.parseLong(id) > 0xFFFFFFFFL)) {
            throw new IllegalArgumentException("表号不正确");
        }
    }

    private void checkOthers() throws IllegalArgumentException {
        if (mWorkStartDay < 1 || mWorkStartDay > 31) {
            throw new IllegalArgumentException("开日必须在1~31号之内");
        }

        if (mWorkStopDay < 1 || mWorkStopDay > 31) {
            throw new IllegalArgumentException("关日必须在1~31号之内");
        }

        if (mWorkStartHour < 0 || mWorkStartHour > 23) {
            throw new IllegalArgumentException("开时必须在0~23点之内");
        }

        if (mWorkStopHour < 0 || mWorkStopHour > 23) {
            throw new IllegalArgumentException("关时必须在0~23点之内");
        }

        if (mSleepDurationSecond < 0 || mSleepDurationSecond > 127) {
            throw new IllegalArgumentException("休眠时长必须在0~127秒之内");
        }

        if (mListenDurationMillisecond < 0 || mListenDurationMillisecond > 62) {
            throw new IllegalArgumentException("侦听时长必须在0~62毫秒之内");
        }

        if (mBase < 0 || mBase > 999999.99) {
            throw new IllegalArgumentException("底数必须在0~999999.99立方米之内");
        }
    }

    private byte[] convertId(String id) {
        byte[] bytes = new byte[5];
        if (mFrequency == FREQUENCY_470) {
            Long idLong = Long.parseLong(id);
            bytes[0] = (byte) ((idLong >>> 24) & 0xFF);     // 最高位,无符号右移。
            bytes[1] = (byte) ((idLong >> 16) & 0xFF);      // 次高位
            bytes[2] = (byte) ((idLong >> 8) & 0xFF);       // 次低位
            bytes[3] = (byte) (idLong & 0xFF);              // 最低位
            bytes[4] = (byte) 0x00;
        } else if (mFrequency == FREQUENCY_495) {
            bytes[0] = (byte) (Integer.parseInt(id.substring(0, 2), 16) & 0xFF);
            bytes[1] = (byte) (Integer.parseInt(id.substring(2, 4), 16) & 0xFF);
            bytes[2] = (byte) (Integer.parseInt(id.substring(4, 6), 16) & 0xFF);
            bytes[3] = (byte) (Integer.parseInt(id.substring(6, 8), 16) & 0xFF);
            bytes[4] = (byte) (Integer.parseInt(id.substring(8, 10), 16) & 0xFF);
        }
        return bytes;
    }

    private byte[] convertSystemTime() {
        Calendar calendar = Calendar.getInstance();
        byte[] bytes = new byte[5];
        bytes[0] = (byte) (calendar.get(Calendar.YEAR) - 2000);
        bytes[1] = (byte) (calendar.get(Calendar.MONTH) + 1);
        bytes[2] = (byte) calendar.get(Calendar.DATE);
        bytes[3] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        bytes[4] = (byte) calendar.get(Calendar.MINUTE);
        return bytes;
    }

    private byte[] convertBase() {
        byte[] bytes = new byte[5];
        String strBase = Double.toString(Math.floor(mBase * 100));
        strBase = strBase.substring(strBase.length() - 8);
        bytes[0] = (byte) (Integer.parseInt(strBase.substring(0, 2), 16) & 0xFF);
        bytes[1] = (byte) (Integer.parseInt(strBase.substring(2, 4), 16) & 0xFF);
        bytes[2] = (byte) (Integer.parseInt(strBase.substring(4, 6), 16) & 0xFF);
        bytes[3] = (byte) (Integer.parseInt(strBase.substring(6, 8), 16) & 0xFF);
        bytes[3] = (byte) 0x00;
        return bytes;
    }

    private byte convertSleepDurationSecond() {
        return (byte) (mSleepDurationSecond * 2);
    }

    private byte convertListenDurationMillisecond() {
        return (byte) ((int) Math.round(mListenDurationMillisecond / 0.244));
    }

    private static byte getSum(byte[] array, int offset, int length) {
        int sum = 0;
        for (int i = offset; i < offset + length; i++) {
            sum += (int) array[i];
        }
        return (byte) (sum & 0xFF);
    }


    private Request(Builder builder) {
        mFrequency = builder.mFrequency;
        mWorkStartDay = builder.mWorkStartDay;
        mWorkStopDay = builder.mWorkStopDay;
        mWorkStartHour = builder.mWorkStartHour;
        mWorkStopHour = builder.mWorkStopHour;
        mSleepDurationSecond = builder.mSleepDurationSecond;
        mListenDurationMillisecond = builder.mListenDurationMillisecond;
        mBase = builder.mBase;
    }

    /**
     * 对终端无线模块的请求指令的构造器。
     * 使请求指令中参数的设置更加清晰了然。
     */
    public static class Builder {
        private String mFrequency;
        private int mWorkStartDay;
        private int mWorkStopDay;
        private int mWorkStartHour;
        private int mWorkStopHour;
        private int mSleepDurationSecond;
        private int mListenDurationMillisecond;
        private double mBase;

        public Builder(@Frequency String frequency) {
            mFrequency = frequency;
            if (mFrequency == FREQUENCY_470) {
                mWorkStartDay = FREQUENCY_470_WORK_START_DAY_DEFAULT;
                mWorkStopDay = FREQUENCY_470_WORK_STOP_DAY_DEFAULT;
                mWorkStartHour = FREQUENCY_470_WORK_START_HOUR_DEFAULT;
                mWorkStopHour = FREQUENCY_470_WORK_STOP_HOUR_DEFAULT;
                mSleepDurationSecond = FREQUENCY_470_SLEEP_DURATION_SECOND_DEFAULT;
                mListenDurationMillisecond = FREQUENCY_470_LISTEN_DURATION_MILLISECOND_DEFAULT;
            }
            else if (mFrequency == FREQUENCY_495) {
                mWorkStartDay = FREQUENCY_495_WORK_START_DAY_DEFAULT;
                mWorkStopDay = FREQUENCY_495_WORK_STOP_DAY_DEFAULT;
                mWorkStartHour = FREQUENCY_495_WORK_START_HOUR_DEFAULT;
                mWorkStopHour = FREQUENCY_495_WORK_STOP_HOUR_DEFAULT;
                mSleepDurationSecond = FREQUENCY_495_SLEEP_DURATION_SECOND_DEFAULT;
                mListenDurationMillisecond = FREQUENCY_495_LISTEN_DURATION_MILLISECOND_DEFAULT;
            }
            mBase = BASE_DEFAULT;
        }

        /**
         * 设置终端无线模块开始工作日期
         * @param workStartDay 开始工作日期（1~31范围）
         * @return Request构造器
         */
        public Builder setWorkStartDay(int workStartDay) {
            mWorkStartDay = workStartDay;
            return this;
        }

        /**
         * 设置终端无线模块停止工作日期
         * @param workStopDay 停止工作日期（1~31范围）
         * @return Request构造器
         */
        public Builder setWorkStopDay(int workStopDay) {
            mWorkStopDay = workStopDay;
            return this;
        }

        /**
         * 设置终端无线模块开始工作时间
         * @param workStartHour 开始工作时间（0~23范围）
         * @return Request构造器
         */
        public Builder setWorkStartHour(int workStartHour) {
            mWorkStartHour = workStartHour;
            return this;
        }

        /**
         * 设置终端无线模块停止工作时间
         * @param workStopHour 停止工作时间（0~23范围）
         * @return Request构造器
         */
        public Builder setWorkStopHour(int workStopHour) {
            mWorkStopHour = workStopHour;
            return this;
        }

        /**
         * 设置终端无线模块在工作时段中的周期性休眠时长
         * @param sleepDurationSecond 周期性休眠时长（0~127范围）
         * @return Request构造器
         */
        public Builder setSleepDurationSecond(int sleepDurationSecond) {
            mSleepDurationSecond = sleepDurationSecond;
            return this;
        }

        /**
         * 设置终端无线模块在工作时段中的周期性侦听时长
         * @param listenDurationMillisecond 周期性侦听时长（0~62范围）
         * @return Request构造器
         */
        public Builder setListenDurationMillisecond(int listenDurationMillisecond) {
            mListenDurationMillisecond = listenDurationMillisecond;
            return this;
        }

        /**
         * 设置终端无线模块的计数器底数
         * @param base 计数器底数
         * @return Request构造器
         */
        public Builder setBase(double base) {
            mBase = base;
            return this;
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
        checkOperation(operation);
        checkId(id);
        checkOthers();

        byte[] array = null;
        switch (operation) {
            case READ_VALUE_WITH_SIGNAL:
                //region 读表带信号返回
                if (mFrequency == FREQUENCY_495) {
                    array = new byte[11];
                    array[0] = (byte) 0x18;
                    array[1] = (byte) 0x09;
                    array[2] = (byte) 0xFD;
                    array[3] = (byte) 0x00;
                    array[4] = (byte) 0x05;
                    System.arraycopy(convertId(id), 0, array, 5, 5);
                    array[10] = getSum(array, 1, 9);
                }
                else if (mFrequency == FREQUENCY_470) {
                    array = new byte[20];
                    array[0] = (byte) 0x18;
                    array[1] = (byte) 0x12;
                    array[2] = (byte) 0x02;
                    array[3] = (byte) 0x00;
                    array[4] = (byte) 0x05;
                    System.arraycopy(convertId(id), 0, array, 5, 5);
                    System.arraycopy(convertSystemTime(), 0, array, 10, 5);
                    array[15] = (byte) mWorkStartDay;
                    array[16] = (byte) mWorkStopDay;
                    array[17] = (byte) mWorkStartHour;
                    array[18] = (byte) mWorkStopHour;
                    array[19] = getSum(array, 1, 18);
                }
                //endregion
                break;
            case READ_VALUE:
                //region 读表不带信号返回
                array = new byte[20];
                array[0] = (byte) 0x18;
                array[1] = (byte) 0x12;
                array[2] = (byte) 0x02;
                array[3] = (byte) 0x00;
                array[4] = (byte) 0x05;
                System.arraycopy(convertId(id), 0, array, 5, 5);
                System.arraycopy(convertSystemTime(), 0, array, 10, 5);
                array[15] = (byte) mWorkStartDay;
                array[16] = (byte) mWorkStopDay;
                array[17] = (byte) mWorkStartHour;
                array[18] = (byte) mWorkStopHour;
                array[19] = getSum(array, 1, 18);
                //endregion
                break;
            case READ_TIME:
                //region 读时
                array = new byte[20];
                array[0] = (byte) 0x18;
                array[1] = (byte) 0x12;
                array[2] = (byte) 0x05;
                array[3] = (byte) 0x00;
                array[4] = (byte) 0x05;
                System.arraycopy(convertId(id), 0, array, 5, 5);
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
                //endregion
                break;
            case OPEN_VALVE:
                //region 开阀
                array = new byte[20];
                array[0] = (byte) 0x18;
                array[1] = (byte) 0x12;
                array[2] = (byte) 0x07;
                array[3] = (byte) 0x00;
                array[4] = (byte) 0x05;
                System.arraycopy(convertId(id), 0, array, 5, 5);
                System.arraycopy(convertSystemTime(), 0, array, 10, 5);
                array[15] = (byte) mWorkStartDay;
                array[16] = (byte) mWorkStopDay;
                array[17] = (byte) mWorkStartHour;
                array[18] = (byte) mWorkStopHour;
                array[19] = getSum(array, 1, 18);
                //endregion
                break;
            case CLOSE_VALVE:
                //region 关阀
                array = new byte[20];
                array[0] = (byte) 0x18;
                array[1] = (byte) 0x12;
                array[2] = (byte) 0x08;
                array[3] = (byte) 0x00;
                array[4] = (byte) 0x05;
                System.arraycopy(convertId(id), 0, array, 5, 5);
                System.arraycopy(convertSystemTime(), 0, array, 10, 5);
                array[15] = (byte) mWorkStartDay;
                array[16] = (byte) mWorkStopDay;
                array[17] = (byte) mWorkStartHour;
                array[18] = (byte) mWorkStopHour;
                array[19] = getSum(array, 1, 18);
                //endregion
                break;
            case EDIT_ID:
                //region 设置表号
                String factoryId = mFrequency == FREQUENCY_470 ?
                        FREQUENCY_470_FACTORY_ID : FREQUENCY_495_FACTORY_ID;
                array = new byte[25];
                array[0] = (byte) 0x18;
                array[1] = (byte) 0x17;
                array[2] = (byte) 0x04;
                array[3] = (byte) 0x00;
                array[4] = (byte) 0x05;
                System.arraycopy(convertId(factoryId), 0, array, 5, 5);
                System.arraycopy(convertId(id), 0, array, 10, 5);
                System.arraycopy(convertSystemTime(), 0, array, 15, 5);
                array[20] = (byte) mWorkStartDay;
                array[21] = (byte) mWorkStopDay;
                array[22] = (byte) mWorkStartHour;
                array[23] = (byte) mWorkStopHour;
                array[24] = getSum(array, 1, 23);
                //endregion
                break;
            case EDIT_BASE:
                //region 设置底数
                array = new byte[25];
                array[0] = (byte) 0x18;
                array[1] = (byte) 0x17;
                array[2] = (byte) 0x03;
                array[3] = (byte) 0x00;
                array[4] = (byte) 0x05;
                System.arraycopy(convertId(id), 0, array, 5, 5);
                System.arraycopy(convertBase(), 0, array, 10, 5);
                System.arraycopy(convertSystemTime(), 0, array, 15, 5);
                array[20] = (byte) mWorkStartDay;
                array[21] = (byte) mWorkStopDay;
                array[22] = (byte) mWorkStartHour;
                array[23] = (byte) mWorkStopHour;
                array[24] = getSum(array, 1, 23);
                //endregion
                break;
            case EDIT_TIME:
                //region 设置时间
                array = new byte[22];
                array[0] = (byte) 0x18;
                array[1] = (byte) 0x14;
                array[2] = (byte) 0xFC;
                array[3] = (byte) 0x00;
                array[4] = (byte) 0x05;
                System.arraycopy(convertId(id), 0, array, 5, 5);
                array[10] = convertSleepDurationSecond();
                array[11] = convertListenDurationMillisecond();
                array[12] = (byte) mWorkStartHour;
                array[13] = (byte) mWorkStopHour;
                array[14] = (byte) mWorkStartDay;
                array[15] = (byte) mWorkStopDay;
                System.arraycopy(convertSystemTime(), 0, array, 16, 5);
                array[21] = getSum(array, 1, 20);
                //endregion
                break;
            case READ_VALUE_FROM_ZAOFU:
                //region 用兆富盒子读表
                array = new byte[15];
                array[0] = (byte) 0x54;
                array[1] = (byte) 0x71;
                array[2] = (byte) 0xED;
                array[3] = (byte) 0x0B;
                System.arraycopy(convertId(id), 0, array, 4, 5);
                array[9] = (byte) 0x15;
                System.arraycopy(convertSystemTime(), 0, array, 10, 5);
                //endregion
                break;
            default:
                break;
        }
        return array;
    }

}