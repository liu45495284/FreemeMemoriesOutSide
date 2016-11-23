package com.freeme.Address;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * ClassName: CodeUtil
 * Description:
 * Author: connorlin
 * Date: Created on 2016-6-23.
 */
public class CodeUtil {

    public static String encode(String str) {
        return encode(str, "utf-8");
    }

    public static String encode(String str, String format) {
        try {
            return URLEncoder.encode(str, format);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;
    }

    public static String decode(String string) {
        return decode(string, "utf-8");
    }

    public static String decode(String string, String format) {
        try {
            return URLDecoder.decode(string, format);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return string;
    }
}
