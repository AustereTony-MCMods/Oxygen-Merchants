package austeretony.oxygen_merchants.server;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import austeretony.oxygen_merchants.common.EnumAction;
import austeretony.oxygen_merchants.common.config.MerchantsConfig;
import austeretony.oxygen_merchants.common.main.EnumMerchantsStatusMessage;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.client.CPEntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;

public class BoundEntitiesManagerServer {

    private final MerchantsManagerServer manager;

    protected BoundEntitiesManagerServer(MerchantsManagerServer manager) {
        this.manager = manager;
    }

    public void merchantProfileEdited(long oldProfileId, long newProfileId) {
        Set<BoundEntityEntry> cache = new HashSet<>();

        for (BoundEntityEntry entry : this.manager.getBoundEntitiesContainer().getEntries())
            if (entry.getMerchantProfileId() == oldProfileId)
                cache.add(entry);

        BoundEntityEntry copy;//required?
        for (BoundEntityEntry entry : cache) {
            this.manager.getBoundEntitiesContainer().removeEntry(entry.getId());
            copy = entry.copy();
            copy.setMerchantProfileId(newProfileId);
            copy.setId(this.manager.getBoundEntitiesContainer().createId(entry.getId()));
            this.manager.getBoundEntitiesContainer().addEntry(copy);
        }

        if (!cache.isEmpty())
            this.manager.getBoundEntitiesContainer().setChanged(true);
    }

    public void merchantProfileRemoved(long profileId) {
        Set<BoundEntityEntry> cache = new HashSet<>();

        for (BoundEntityEntry entry : this.manager.getBoundEntitiesContainer().getEntries())
            if (entry.getMerchantProfileId() == profileId)
                cache.add(entry);

        BoundEntityEntry copy;
        for (BoundEntityEntry entry : cache) {
            this.manager.getBoundEntitiesContainer().removeEntry(entry.getId());
            copy = entry.copy();
            copy.setMerchantProfileId(0L);
            copy.setId(this.manager.getBoundEntitiesContainer().createId(entry.getId()));
            this.manager.getBoundEntitiesContainer().addEntry(copy);
        }

        if (!cache.isEmpty())
            this.manager.getBoundEntitiesContainer().setChanged(true);
    }

    public void informPlayer(EntityPlayerMP playerMP, EnumMerchantsStatusMessage status) {
        OxygenHelperServer.sendStatusMessage(playerMP, MerchantsMain.MERCHANTS_MOD_INDEX, status.ordinal());
    }

    public void createEntry(EntityPlayerMP playerMP, int entityId, String name, String profession, long profileId) {
        if (CommonReference.isPlayerOpped(playerMP)
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()) {
            Entity pointed = playerMP.world.getEntityByID(entityId);
            if (pointed != null 
                    && pointed instanceof EntityLiving
                    && !this.manager.getBoundEntitiesContainer().entryExist(CommonReference.getPersistentUUID(pointed))) {
                BoundEntityEntry entry = new BoundEntityEntry(CommonReference.getPersistentUUID(pointed), pointed.dimension, (int) pointed.posX, (int) pointed.posY, (int) pointed.posZ);
                entry.setId(System.currentTimeMillis());
                entry.setName(name);
                entry.setProfession(profession);
                entry.setMerchantProfileId(profileId);
                this.manager.getBoundEntitiesContainer().addEntry(entry);

                ((EntityLiving) pointed).enablePersistence();

                OxygenMain.network().sendTo(new CPEntityAction(EnumAction.CREATED, entry), playerMP); 
                this.informPlayer(playerMP, EnumMerchantsStatusMessage.ENTITY_CREATED);

                this.manager.getBoundEntitiesContainer().setChanged(true);

                MerchantsMain.LOGGER.info("Bond creration - SERVER. Entity id: {}, uuid: {}.", pointed.getEntityId(), pointed.getPersistentID());//TODO DEBUG
            }
        }
    }

    public void editEntry(EntityPlayerMP playerMP, long oldBondId, String name, String profession, long profileId) {
        if (CommonReference.isPlayerOpped(playerMP) 
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()
                && this.manager.getBoundEntitiesContainer().entryExist(oldBondId)) {
            BoundEntityEntry entry = this.manager.getBoundEntitiesContainer().getBoundEntityEntry(oldBondId);
            this.manager.getBoundEntitiesContainer().removeEntry(oldBondId);
            entry.setId(oldBondId + 1L);
            entry.setName(name);
            entry.setProfession(profession);
            entry.setMerchantProfileId(profileId);
            this.manager.getBoundEntitiesContainer().addEntry(entry);

            OxygenMain.network().sendTo(new CPEntityAction(EnumAction.UPDATED, entry), playerMP); 
            this.informPlayer(playerMP, EnumMerchantsStatusMessage.ENTITY_UPDATED);

            this.manager.getBoundEntitiesContainer().setChanged(true);
        }
    }

    public void visitEntity(EntityPlayerMP playerMP, long bondId) {
        if (CommonReference.isPlayerOpped(playerMP)
                && this.manager.getBoundEntitiesContainer().entryExist(bondId)) {
            final BoundEntityEntry entry = this.manager.getBoundEntitiesContainer().getBoundEntityEntry(bondId);
            //TODO Add entity search and move to it directly
            CommonReference.delegateToServerThread(()->CommonReference.teleportPlayer(playerMP, entry.getDimension(), entry.getXPos(), entry.getYPos(), entry.getZPos()));
        }
    }

    public void removeEntry(EntityPlayerMP playerMP, long bondId) {
        if (CommonReference.isPlayerOpped(playerMP)
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()
                && this.manager.getBoundEntitiesContainer().entryExist(bondId)) {
            BoundEntityEntry entry = this.manager.getBoundEntitiesContainer().getBoundEntityEntry(bondId);
            this.manager.getBoundEntitiesContainer().removeEntry(bondId);

            OxygenMain.network().sendTo(new CPEntityAction(EnumAction.REMOVED, entry), playerMP); 
            this.informPlayer(playerMP, EnumMerchantsStatusMessage.ENTITY_REMOVED);

            this.manager.getBoundEntitiesContainer().setChanged(true);
        }
    }

    public void entityLivingDied(UUID entityUUID) {
        if (this.manager.getBoundEntitiesContainer().entryExist(entityUUID)) {
            BoundEntityEntry entry = this.manager.getBoundEntitiesContainer().getBoundEntityEntry(entityUUID);
            long oldBondId = entry.getId();
            this.manager.getBoundEntitiesContainer().removeEntry(oldBondId);
            entry.setId(oldBondId + 1L);
            entry.markDead();
            this.manager.getBoundEntitiesContainer().addEntry(entry);
            this.manager.getBoundEntitiesContainer().setChanged(true);
        }
    }
}
