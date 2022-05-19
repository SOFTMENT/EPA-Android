
package com.application.epa.Models.Stripe;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Networks {

    @SerializedName("available")
    @Expose
    private List<String> available = null;
    @SerializedName("selectionMandatory")
    @Expose
    private Boolean selectionMandatory;

    public List<String> getAvailable() {
        return available;
    }

    public void setAvailable(List<String> available) {
        this.available = available;
    }

    public Boolean getSelectionMandatory() {
        return selectionMandatory;
    }

    public void setSelectionMandatory(Boolean selectionMandatory) {
        this.selectionMandatory = selectionMandatory;
    }

}
