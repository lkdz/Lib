package com.lkdz.util;

/**
 * 字符串格式化工具
 */
public class StringFormatter {
    /**
     * 将字节数组的每个字节用指定的字符串相连并以HEX字符形式显示
     * @param array 指定的字节数组
     * @param concatChar 指定的连接字符串
     * @return 字节数组的字符串形式
     */
    public static String toString(byte[] array, String concatChar) {
        if (array == null || array.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte byt: array) {
            sb.append(String.format("%02X", byt));
            sb.append(concatChar);
        }
        return sb.toString().substring(0, sb.length() - concatChar.length());
    }
}
