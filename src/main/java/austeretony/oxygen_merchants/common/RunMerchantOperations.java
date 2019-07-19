package austeretony.oxygen_merchants.common;

import austeretony.oxygen.common.api.process.AbstractPersistentProcess;

public class RunMerchantOperations extends AbstractPersistentProcess {

    @Override
    public boolean hasDelay() {
        return false;
    }

    @Override
    public int getExecutionDelay() {
        return 0;
    }

    @Override
    public void execute() {
        MerchantsManagerServer.instance().runMerchantOperations();
    }
}
