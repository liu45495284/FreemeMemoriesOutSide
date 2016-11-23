package com.freeme.provider;

/**
 * Created by Kathy on 16-11-8.
 */
public class AddressObject {
    private Long id;
    private String country;
    private String city;

    public AddressObject() {

    }
    public AddressObject(Long id, String country, String city ) {
        this.id = id;
        this.country = country;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "id = " + id + " country = " + country + " city = " + city;
    }
}
