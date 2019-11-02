package austeretony.oxygen_merchants.server;

import austeretony.oxygen_merchants.common.EnumMerchantOperation;

public class QueuedMerchantOperation {

    final EnumMerchantOperation operation;

    final long profileId, offerId;

    QueuedMerchantOperation(EnumMerchantOperation operation, long profileId, long offerId) {
        this.operation = operation;
        this.profileId = profileId;
        this.offerId = offerId;
    }
}
