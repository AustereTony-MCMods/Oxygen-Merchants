package austeretony.oxygen_merchants.client.interaction;

import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.interaction.IInteraction;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class MerchantInteraction implements IInteraction {

    private Entity pointed;

    @Override
    public boolean isValid() {
        this.pointed = ClientReference.getPointedEntity();
        return this.pointed != null 
                && this.pointed instanceof EntityLiving 
                && MerchantsManagerClient.instance().getBoundEntitiesManager().bondExist(ClientReference.getPersistentUUID(this.pointed));
    }

    @Override
    public void execute() {
        BoundEntityEntry entry = MerchantsManagerClient.instance().getBoundEntitiesManager().getBond(ClientReference.getPersistentUUID(this.pointed));   
        MerchantsManagerClient.instance().openMerchantMenuSynced(ClientReference.getEntityId(this.pointed), 
                MerchantsManagerClient.instance().getMerchantProfilesManager().profileExist(entry.getProfileId()) ? entry.getProfileId() : 0L);
    }
}
