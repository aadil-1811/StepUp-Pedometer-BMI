package com.example.walkbuddy;

public class Utility {
    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    public static String zeroX(int i) {
        String str = "";
        if (i > 9)
            str = Integer.toString(i);
        else
            str = "0" + Integer.toString(i);
        return str;
    }


}
