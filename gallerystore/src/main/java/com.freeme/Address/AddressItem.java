package com.freeme.Address;

/**
 * ClassName: AddressInfo
 * Description:
 * Author: connorlin
 * Date: Created on 2016-6-2.
 */
public class AddressItem {

    /**
     * long_name : 1677
     * short_name : 1677
     * types : ["street_number"]
     */

    private String long_name;
    private String short_name;
//    private List<String> types;
    private String[] types;

    public String getLong_name() {
        return long_name;
    }

    public void setLong_name(String long_name) {
        this.long_name = long_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    //    public List<String> getTypes() {
//        return types;
//    }
//
//    public void setTypes(List<String> types) {
//        this.types = types;
//    }
}
