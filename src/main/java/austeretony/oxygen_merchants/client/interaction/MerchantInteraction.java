package austeretony.oxygen_merchants.client.interaction;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.interaction.Interaction;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class MerchantInteraction implements Interaction {

    private Entity pointed;

    @Override
    public boolean isValid() {
        this.pointed = ClientReference.getPointedEntity();
        return this.pointed != null 
                && this.pointed instanceof EntityLiving 
                && MerchantsManagerClient.instance().getBoundEntitiesContainer().entryExist(ClientReference.getPersistentUUID(this.pointed));
    }

    @Override
    public void execute() {
        BoundEntityEntry entry = MerchantsManagerClient.instance().getBoundEntitiesContainer().getBoundEntityEntry(ClientReference.getPersistentUUID(this.pointed));   
        MerchantsManagerClient.instance().getMenuManager().openMerchantMenuSynced(ClientReference.getEntityId(this.pointed), 
                MerchantsManagerClient.instance().getMerchantProfilesContainer().profileExist(entry.getProfileId()) ? entry.getProfileId() : 0L);
    }
}
