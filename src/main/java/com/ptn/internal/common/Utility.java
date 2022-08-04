package com.ptn.internal.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    public static String getValueFromRegex(String str, Pattern pattern) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
