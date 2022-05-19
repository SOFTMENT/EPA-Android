
package com.application.epa.Models.Stripe;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PaymentMethod {

    @SerializedName("billingDetails")
    @Expose
    private BillingDetails billingDetails;
    @SerializedName("card")
    @Expose
    private Card card;
    @SerializedName("created")
    @Expose
    private Integer created;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("liveMode")
    @Expose
    private Boolean liveMode;
    @SerializedName("type")
    @Expose
    private String type;

    public BillingDetails getBillingDetails() {
        return billingDetails;
    }

    public void setBillingDetails(BillingDetails billingDetails) {
        this.billingDetails = billingDetails;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getLiveMode() {
        return liveMode;
    }

    public void setLiveMode(Boolean liveMode) {
        this.liveMode = liveMode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
