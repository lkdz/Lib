package com.lkdz.iso7064;

/**
 * Created by DELL on 2016/5/30.
 */
public class CheckDigits {
    public static final String AlphaCharSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String AlphanumericCharSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String HexCharSet = "0123456789ABCDEF";
    public static final String Mod112CharSet = "0123456789X";
    public static final String Mod372CharSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ*";
    public static final String NumericCharSet = "0123456789";

    public static String CalculateAlphaCheckDigit(String value, boolean doubleDigit) {
        return CalculateCheckDigit(value, "ABCDEFGHIJKLMNOPQRSTUVWXYZ", doubleDigit);
    }

    public static String CalculateAlphanumericCheckDigit(String value, boolean doubleDigit) {
        return CalculateCheckDigit(value, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", doubleDigit);
    }

    public static String CalculateCheckDigit(String value, String charSet, boolean doubleDigit) {
        int num = 0;
        int num2 = 0;
        RadixAndModulus radixAndModulus = GetCheckDigitRadixAndModulus(charSet, doubleDigit, num, num2);
        num = radixAndModulus.getRadix();
        num2 = radixAndModulus.getModulus();
        if (num2 != (num + 1)) {
            return CalculatePureSystemCheckDigit(value, num, num2, charSet, doubleDigit);
        }
        return CalculateHybridSystemCheckDigit(value, charSet);
    }

    public static String CalculateHexCheckDigit(String value, boolean doubleDigit) {
        return CalculateCheckDigit(value, "0123456789ABCDEF", doubleDigit);
    }

    public static String CalculateHybridSystemCheckDigit(String value, String charSet) {
        if (value == null || value.length() == 0) {
            return null;
        }
        value = value.toUpperCase();
        int length = charSet.length();
        int num2 = length;
        for (char ch : value.toCharArray()) {
            int index = charSet.indexOf(ch);
            if (index == -1) {
                return null;
            }
            num2 += index;
            if (num2 > length) {
                num2 -= length;
            }
            num2 *= 2;
            if (num2 >= (length + 1)) {
                num2 -= length + 1;
            }
        }
        num2 = (length + 1) - num2;
        if (num2 == length) {
            num2 = 0;
        }
        return (value + charSet.charAt(num2));
    }

    public static String CalculateNumericCheckDigit(String value, boolean doubleDigit) {
        return CalculateCheckDigit(value, "0123456789", doubleDigit);
    }

    public static String CalculatePureSystemCheckDigit(String value, int radix, int modulus, String charSet, boolean doubleDigit) {
        if (value == null || value.length() == 0) {
            return null;
        }
        value = value.toUpperCase();
        int num = 0;
        for (char ch : value.toCharArray()) {
            int index = charSet.indexOf(ch);
            if (index == -1) {
                return null;
            }
            num = ((num + index) * radix) % modulus;
        }
        if (doubleDigit) {
            num = (num * radix) % modulus;
        }
        int num3 = ((modulus - num) + 1) % modulus;
        if (doubleDigit) {
            int num4 = num3 % radix;
            int num5 = (num3 - num4) / radix;
            return (value + charSet.charAt(num5) + charSet.charAt(num4));
        }
        return (value + charSet.charAt(num3));
    }

    private static RadixAndModulus GetCheckDigitRadixAndModulus(String charSet, boolean doubleDigit, int radix, int modulus) {
        RadixAndModulus radixAndModulus = new RadixAndModulus();
        radix = charSet.length();
        modulus = radix + 1;
        if (doubleDigit) {
            switch (radix) {
                case 0x1a:
                    modulus = 0x295;
                    break;

                case 0x24:
                    modulus = 0x4f7;
                    break;

                case 10:
                    modulus = 0x61;
                    break;

                case 0x10:
                    modulus = 0xfb;
                    break;
            }
        }
        else if (radix == 11) {
            modulus = 11;
            radix = 2;
        }
        else if (radix == 0x25) {
            modulus = 0x25;
            radix = 2;
        }
        if ((((radix != 2) && (radix != 10)) && ((radix != 0x10) && (radix != 0x1a))) && (radix != 0x24)) {
            throw new IllegalArgumentException("Invalid character set.");
        }
        radixAndModulus.setRadix(radix);
        radixAndModulus.setModulus(modulus);
        return radixAndModulus;
    }

    public static class RadixAndModulus {
        public int getRadix() {
            return radix;
        }

        public void setRadix(int radix) {
            this.radix = radix;
        }

        public int getModulus() {
            return modulus;
        }

        public void setModulus(int modulus) {
            this.modulus = modulus;
        }

        public int radix;
        public int modulus;
    }

    public static boolean VerifyAlphaCheckDigit(String value, boolean doubleDigit) {
        return VerifyCheckDigit(value, "ABCDEFGHIJKLMNOPQRSTUVWXYZ", doubleDigit);
    }

    public static boolean VerifyAlphanumericCheckDigit(String value, boolean doubleDigit) {
        return VerifyCheckDigit(value, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", doubleDigit);
    }

    public static boolean VerifyCheckDigit(String value, String charSet, boolean doubleDigit) {
        int num = 0;
        int num2 = 0;
        RadixAndModulus radixAndModulus = GetCheckDigitRadixAndModulus(charSet, doubleDigit, num, num2);
        num = radixAndModulus.getRadix();
        num2 = radixAndModulus.getModulus();
        return VerifyCheckDigit(value, num, num2, charSet, doubleDigit);
    }

    public static boolean VerifyCheckDigit(String value, int radix, int modulus, String charSet, boolean doubleDigit) {
        int num = doubleDigit ? 2 : 1;
        if ((value == null) || (value.length() <= num)) {
            return false;
        }
        value = value.toUpperCase();
        String str = value.substring(0, value.length() - num);
        if (modulus != (radix + 1)) {
            return (value == CalculatePureSystemCheckDigit(str, radix, modulus, charSet, doubleDigit));
        }
        return (value == CalculateHybridSystemCheckDigit(str, charSet));
    }

    public static boolean VerifyHexCheckDigit(String value, boolean doubleDigit) {
        return VerifyCheckDigit(value, "0123456789ABCDEF", doubleDigit);
    }

    public static boolean VerifyNumericCheckDigit(String value, boolean doubleDigit) {
        return VerifyCheckDigit(value, "0123456789", doubleDigit);
    }
}
