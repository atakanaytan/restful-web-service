package com.mobile.app.ws.mobileappws.io.entity;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "addresses")
public class AddressEntity implements Serializable {

    private static final long serialVersionUID = -3382144808349856390L;

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "address_id", length = 30, nullable = false)
    private String addressId;

    @Column(name = "country", length = 15, nullable = false)
    private String country;

    @Column(name = "city", length = 15, nullable = false)
    private String city;

    @Column(name = "street_name", length = 100, nullable = false)
    private String streetName;

    @Column(name = "postal_code", length = 7, nullable = false)
    private String postalCode;

    @Column(name = "type", length = 10, nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userDetails;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
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

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }
}
