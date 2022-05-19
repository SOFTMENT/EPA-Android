
package com.application.epa.Models.Stripe;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StripeModel {

    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("canceledAt")
    @Expose
    private Integer canceledAt;
    @SerializedName("captureMethod")
    @Expose
    private String captureMethod;
    @SerializedName("clientSecret")
    @Expose
    private String clientSecret;
    @SerializedName("confirmationMethod")
    @Expose
    private String confirmationMethod;
    @SerializedName("created")
    @Expose
    private Integer created;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("isLiveMode")
    @Expose
    private Boolean isLiveMode;
    @SerializedName("paymentMethod")
    @Expose
    private PaymentMethod paymentMethod;
    @SerializedName("paymentMethodId")
    @Expose
    private String paymentMethodId;
    @SerializedName("paymentMethodTypes")
    @Expose
    private List<String> paymentMethodTypes = null;
    @SerializedName("status")
    @Expose
    private String status;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(Integer canceledAt) {
        this.canceledAt = canceledAt;
    }

    public String getCaptureMethod() {
        return captureMethod;
    }

    public void setCaptureMethod(String captureMethod) {
        this.captureMethod = captureMethod;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getConfirmationMethod() {
        return confirmationMethod;
    }

    public void setConfirmationMethod(String confirmationMethod) {
        this.confirmationMethod = confirmationMethod;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsLiveMode() {
        return isLiveMode;
    }

    public void setIsLiveMode(Boolean isLiveMode) {
        this.isLiveMode = isLiveMode;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public List<String> getPaymentMethodTypes() {
        return paymentMethodTypes;
    }

    public void setPaymentMethodTypes(List<String> paymentMethodTypes) {
        this.paymentMethodTypes = paymentMethodTypes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
