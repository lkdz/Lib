package com.lkdz.util;

/**
 * Created by DELL on 2016/6/10.
 */
public class StringFormatter {
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
