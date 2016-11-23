package com.freeme.memories.utils;

import com.freeme.memories.constant.Global;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-20.
 */
public class AppUtil {

    public static String getDataPath(String dataUrl) {
        return Global.FILE_PREX + dataUrl;
    }

    public static int[] noRepeatRandom(int rang) {
        int[] templetId = new int[rang];
        int n = rang;
        Random rand = new Random();
        boolean[] bool = new boolean[n];
        int num = 0;
        for (int i = 0; i < n; i++) {
            while (bool[num]) {
                num = rand.nextInt(n);
            }
            bool[num] = true;
            templetId[i] = num;
        }

        return templetId;
    }

    public static void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (IOException t) {
            LogUtil.e("close fail " + t);
        }
    }

    public static byte[] getBytes(String in) {
        byte[] result = new byte[in.length() * 2];
        int output = 0;
        for (char ch : in.toCharArray()) {
            result[output++] = (byte) (ch & 0xFF);
            result[output++] = (byte) (ch >> 8);
        }
        return result;
    }
}
