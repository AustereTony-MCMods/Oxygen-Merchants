package austeretony.oxygen_merchants.server;

import austeretony.oxygen_merchants.common.EnumMerchantOperation;

public class QueuedMerchantOperation {

    final EnumMerchantOperation operation;

    final long offerId;

    QueuedMerchantOperation(EnumMerchantOperation operation, long offerId) {
        this.operation = operation;
        this.offerId = offerId;
    }
}
