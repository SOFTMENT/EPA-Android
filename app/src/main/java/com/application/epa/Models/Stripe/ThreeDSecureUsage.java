
package com.application.epa.Models.Stripe;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ThreeDSecureUsage {

    @SerializedName("isSupported")
    @Expose
    private Boolean isSupported;

    public Boolean getIsSupported() {
        return isSupported;
    }

    public void setIsSupported(Boolean isSupported) {
        this.isSupported = isSupported;
    }

}
