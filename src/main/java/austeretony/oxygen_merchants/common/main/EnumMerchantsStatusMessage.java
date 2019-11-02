package austeretony.oxygen_merchants.common.main;

import austeretony.oxygen_core.client.api.ClientReference;

public enum EnumMerchantsStatusMessage {

    PROFILE_CREATED("profileCreated"),
    PROFILE_UPDATED("profileUpdated"),
    PROFILE_REMOVED("profileRemoved"),
    ENTITY_CREATED("entityCreated"),
    ENTITY_UPDATED("entityUpdated"),
    ENTITY_REMOVED("entityRemoved");

    private final String status;

    EnumMerchantsStatusMessage(String status) {
        this.status = "oxygen_merchants.status." + status;
    }

    public String localizedName() {
        return ClientReference.localize(this.status);
    }
}
