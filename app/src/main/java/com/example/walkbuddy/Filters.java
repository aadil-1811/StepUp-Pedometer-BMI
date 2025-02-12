package com.example.walkbuddy;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.Locale;

public class Filters {
    public static InputFilter[] EmojiFilter()
    {
        InputFilter EMOJI_FILTER = (source, start, end, dest, dstart, dend) -> {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE || type==Character.NON_SPACING_MARK
                        || type==Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        };
        return new InputFilter[]{EMOJI_FILTER};
    }

    public static InputFilter[] UpperEmojiFilter()
    {
        InputFilter UPPER_FILTER = (source, start, end, dest, dstart, dend) -> {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));
                char ch = source.charAt(index);
                if (Character.isUpperCase(ch)) {
                    String str=String.valueOf(source);

                    String s=String.valueOf(ch);
                    str = str.substring(0, index) + s.toLowerCase(Locale.ROOT)
                            + str.substring(index + 1);
                    return str;
                }
                if (type == Character.SURROGATE || type==Character.NON_SPACING_MARK
                        || type==Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        };
        return new InputFilter[]{UPPER_FILTER};
    }

}
