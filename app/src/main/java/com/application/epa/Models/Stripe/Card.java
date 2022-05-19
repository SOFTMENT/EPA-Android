
package com.application.epa.Models.Stripe;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;




public class Card {

    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("checks")
    @Expose
    private Checks checks;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("expiryMonth")
    @Expose
    private Integer expiryMonth;
    @SerializedName("expiryYear")
    @Expose
    private Integer expiryYear;
    @SerializedName("funding")
    @Expose
    private String funding;
    @SerializedName("last4")
    @Expose
    private String last4;
    @SerializedName("networks")
    @Expose
    private Networks networks;
    @SerializedName("threeDSecureUsage")
    @Expose
    private ThreeDSecureUsage threeDSecureUsage;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Checks getChecks() {
        return checks;
    }

    public void setChecks(Checks checks) {
        this.checks = checks;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(Integer expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public Integer getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(Integer expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public Networks getNetworks() {
        return networks;
    }

    public void setNetworks(Networks networks) {
        this.networks = networks;
    }

    public ThreeDSecureUsage getThreeDSecureUsage() {
        return threeDSecureUsage;
    }

    public void setThreeDSecureUsage(ThreeDSecureUsage threeDSecureUsage) {
        this.threeDSecureUsage = threeDSecureUsage;
    }

}
