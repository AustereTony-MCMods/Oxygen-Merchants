package austeretony.oxygen_merchants.client;

import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.server.SPCreateBond;
import austeretony.oxygen_merchants.common.network.server.SPEditBond;
import austeretony.oxygen_merchants.common.network.server.SPRemoveBond;
import austeretony.oxygen_merchants.common.network.server.SPVisitEntity;
import net.minecraft.entity.Entity;

public class BoundEntitiesManagerClient {

    private MerchantsManagerClient manager;

    protected BoundEntitiesManagerClient(MerchantsManagerClient manager) {
        this.manager = manager;
    }

    public void merchantProfileEdited(long oldProfileId) {
        boolean changed = false;
        for (BoundEntityEntry entry : this.manager.getBoundEntitiesContainer().getEntries()) {
            if (entry.getProfileId() == oldProfileId) {
                entry.setProfileId(oldProfileId + 1L);
                changed = true;
            }
        }
        if (changed)
            this.manager.getBoundEntitiesContainer().setChanged(true);
    }

    public void merchantProfileRemoved(long profileId) {
        boolean changed = false;
        for (BoundEntityEntry entry : this.manager.getBoundEntitiesContainer().getEntries()) {
            if (entry.getProfileId() == profileId) {
                entry.setProfileId(0L);
                this.manager.getBoundEntitiesContainer().removeAccess(entry.getEntityUUID());
                changed = true;
            }
        }
        if (changed)
            this.manager.getBoundEntitiesContainer().setChanged(true);
    }

    public void createEntrySynced(Entity entity, String name, String profession, long profileId) {
        OxygenMain.network().sendToServer(new SPCreateBond(entity.getEntityId(), name, profession, profileId));
        MerchantsMain.LOGGER.info("Bond creration - CLIENT. Entity id: {}, uuid: {}.", entity.getEntityId(), entity.getPersistentID());//TODO DEBUG
    }

    public void editEntrySynced(long oldBondId, String name, String profession, long profileId) {
        OxygenMain.network().sendToServer(new SPEditBond(oldBondId, name, profession, profileId));
    }

    public void visitEntitySynced(long bondId) {
        OxygenMain.network().sendToServer(new SPVisitEntity(bondId));
    }

    public void removeEntrySynced(long bondId) {
        OxygenMain.network().sendToServer(new SPRemoveBond(bondId));
    }

    public void entryCreated(BoundEntityEntry entry) {
        this.manager.getBoundEntitiesContainer().addEntry(entry);
        this.manager.getBoundEntitiesContainer().setChanged(true);
    }

    public void entryRemoved(BoundEntityEntry entry) {
        this.manager.getBoundEntitiesContainer().removeEntry(entry.getId());
        this.manager.getBoundEntitiesContainer().setChanged(true);
    }
}
