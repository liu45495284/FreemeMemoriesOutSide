package com.freeme.Address;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-20.
 */
public class Config {

    public static final String GOOGLE_MAP_API =
            "http://maps.google.cn/maps/api/geocode/json?language=en&latlng=%1$s," + "%2$s";
   /* public static final String GOOGLE_MAP_API_CHINA =
            "http://maps.google.cn/maps/api/geocode/json?language=zh-CN&latlng=%1$s," + "%2$s";*/
/*    public static final String GOOGLE_MAP_API =
            "http://maps.google.cn/maps/api/geocode/json?language=th&latlng=%1$s," + "%2$s";*/
    public final static int CONNECT_TIMEOUT = 20000;
    public final static int READ_TIMEOUT = 20000;

    /**
     * 表示国家政治实体，通常是由地理编码器返回的最高级别类型
     */
    public static final String COUNTRY = "country";

    /**
     * 表示国家/地区级别以下的一级行政实体。在美国，这种行政级别就是州。并非所有国家都设有这类行政级别
     */
    public static final String COUNTRY_AREA_LEVEL_1 = "administrative_area_level_1";

    /**
     * 表示国家/地区级别以下的二级行政实体。在美国，这种行政级别就是县。并非所有国家都设有这类行政级别
     */
    public static final String COUNTRY_AREA_LEVEL_2 = "administrative_area_level_2";

    /**
     * 表示国家/地区级别以下的三级行政实体。在美国，这种行政级别就是县。并非所有国家都设有这类行政级别
     */
    public static final String COUNTRY_AREA_LEVEL_3 = "administrative_area_level_3";

    /**
     * 表示国家/地区级别以下的四级行政实体。在美国，这种行政级别就是县。并非所有国家都设有这类行政级别
     */
    public static final String COUNTRY_AREA_LEVEL_4 = "administrative_area_level_4";

    /**
     * 表示国家/地区级别以下的五级行政实体。在美国，这种行政级别就是县。并非所有国家都设有这类行政级别
     */
    public static final String COUNTRY_AREA_LEVEL_5 = "administrative_area_level_5";

    public static final String LOCALITY = "locality";

    /**
     * 表示 locality 以下的一级行政实体
     * 每个 sublocality 级别都是一个行政实体。数字越大，表示的地理区域越小
     */
    public static final String SUBLOCALITY_LEVEL_1 = "sublocality_level_1";
    public static final String SUBLOCALITY_LEVEL_2 = "sublocality_level_2";
    public static final String SUBLOCALITY_LEVEL_3 = "sublocality_level_3";
    public static final String SUBLOCALITY_LEVEL_4 = "sublocality_level_4";
    public static final String SUBLOCALITY_LEVEL_5 = "sublocality_level_5";

    /**
     * 表示已命名的路线（例如“US 101”）
     */
    public static final String ROUTE = "route";

    /**
     * 门牌号
     */
    public static final String STREET_NUMBER = "street_number";

}
