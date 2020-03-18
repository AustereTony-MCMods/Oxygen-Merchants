package austeretony.oxygen_merchants.common.main;

import austeretony.oxygen_core.client.api.ClientReference;

public enum EnumMerchantsStatusMessage {

    PROFILES_RELOADED("profilesReloaded"),
    PROFILE_CREATED("profileCreated"),
    PROFILE_UPDATED("profileUpdated"),
    PROFILE_SAVED("profileSaved"),
    PROFILE_REMOVED("profileRemoved");

    private final String status;

    EnumMerchantsStatusMessage(String status) {
        this.status = "oxygen_merchants.status.message." + status;
    }

    public String localizedName() {
        return ClientReference.localize(this.status);
    }
}
