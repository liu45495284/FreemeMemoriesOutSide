package com.freeme.Address;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ClassName: AnalyzeUrl
 * Description:
 * Author: connorlin
 * Date: Created on 2016-6-2.
 */
public class AnalyzeUrl {

    public static String getJsonStringFromUrl(String url) {
        URL cityInfoUrl;
        byte[] data = new byte[]{};
        try {
            cityInfoUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) cityInfoUrl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(Config.CONNECT_TIMEOUT);
            connection.setReadTimeout(Config.READ_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("contentType", "utf-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            InputStream inStream = connection.getInputStream();
            data = readInputStream(inStream);
            return (new String(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (new String(data));
    }

    private static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
}
