
package com.application.epa.Models.Stripe;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BillingDetails {

    @SerializedName("address")
    @Expose
    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}
