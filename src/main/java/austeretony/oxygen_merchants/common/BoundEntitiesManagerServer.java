package austeretony.oxygen_merchants.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen.common.api.IPersistentData;
import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.util.ConcurrentSetWrapper;
import austeretony.oxygen.util.StreamUtils;
import austeretony.oxygen_merchants.common.config.MerchantsConfig;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;

public class BoundEntitiesManagerServer implements IPersistentData {

    // <bondId, BoundEntityEntry>
    private final Map<Long, BoundEntityEntry> entities = new ConcurrentHashMap<Long, BoundEntityEntry>();

    // <entityUUID, bondId>
    private final Map<UUID, Long> access = new ConcurrentHashMap<UUID, Long>();

    // <profileId, ConcurrentSetWrapper<bondId>>
    private final Map<Long, ConcurrentSetWrapper<Long>> entriesAccess = new ConcurrentHashMap<Long, ConcurrentSetWrapper<Long>>();

    public int getBondsAmount() {
        return this.entities.size();
    }

    public Set<Long> getBondIds() {
        return this.entities.keySet();
    }

    public Collection<BoundEntityEntry> getBonds() {
        return this.entities.values();
    }

    public boolean bondExist(long bondId) {
        return this.entities.containsKey(bondId);
    }

    public boolean bondExist(UUID entityUUID) {
        return this.access.containsKey(entityUUID);
    }

    public BoundEntityEntry getBond(long bondId) {
        return this.entities.get(bondId);
    }

    public BoundEntityEntry getBond(UUID entityUUID) {
        return this.entities.get(this.access.get(entityUUID));
    }

    public UUID getEntityUUIDById(long bondId) {
        return this.entities.get(bondId).entityUUID;
    }

    public void addBoundEntityEntry(BoundEntityEntry entry) {
        this.entities.put(entry.getId(), entry);
        this.access.put(entry.entityUUID, entry.getId());
        if (entry.getProfileId() != 0L) {
            if (!this.entriesAccess.containsKey(entry.getProfileId())) {   
                ConcurrentSetWrapper<Long> container = new ConcurrentSetWrapper<Long>();
                container.set.add(entry.getId());
                this.entriesAccess.put(entry.getProfileId(), container);
            } else
                this.entriesAccess.get(entry.getProfileId()).add(entry.getId());
        }
    }

    public void removeBoundEntityEntry(long id) {
        BoundEntityEntry entry = this.entities.remove(id);
        if (entry != null) {
            this.access.remove(entry.entityUUID);
            if (entry.getProfileId() != 0L) {
                ConcurrentSetWrapper<Long> container = this.entriesAccess.get(entry.getProfileId());
                container.remove(entry.getId());
                if (container.isEmpty())
                    this.entriesAccess.remove(entry.getProfileId());
            }
        }
    }

    public void merchantProfileEdited(long oldProfileId) {
        if (this.entriesAccess.containsKey(oldProfileId)) {
            for (long bondId : this.entriesAccess.get(oldProfileId).set)
                this.entities.get(bondId).setProfileId(oldProfileId + 1L);
            this.entriesAccess.put(oldProfileId + 1L, this.entriesAccess.remove(oldProfileId));

            OxygenHelperServer.savePersistentDataDelegated(this);
        }
    }

    public void merchantProfileRemoved(long profileId) {
        if (this.entriesAccess.containsKey(profileId)) {
            BoundEntityEntry entry;
            for (long bondId : this.entriesAccess.get(profileId).set) {
                entry = this.entities.get(bondId);
                entry.setProfileId(0L);
                this.access.remove(entry.entityUUID);
            }
            this.entriesAccess.remove(profileId);

            OxygenHelperServer.savePersistentDataDelegated(this);
        }
    }

    public void createBond(EntityPlayerMP playerMP, long bondId, int entityId, String name, String profession, long profileId) {
        if (CommonReference.isOpped(playerMP)
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()) {
            Entity pointed = playerMP.world.getEntityByID(entityId);
            if (pointed != null 
                    && pointed instanceof EntityLiving
                    && !this.bondExist(CommonReference.getPersistentUUID(pointed))) {
                BoundEntityEntry entry = new BoundEntityEntry(CommonReference.getPersistentUUID(pointed), pointed.dimension, (int) pointed.posX, (int) pointed.posY, (int) pointed.posZ);
                entry.setId(bondId);
                entry.setName(name);
                entry.setProfession(profession);
                entry.setProfileId(profileId);
                this.addBoundEntityEntry(entry);

                MerchantsMain.LOGGER.info("Bond creration - SERVER. Entity id: {}, uuid: {}.", pointed.getEntityId(), pointed.getPersistentID());//TODO DEBUG

                OxygenHelperServer.savePersistentDataDelegated(this);
            }
        }
    }

    public void editBond(EntityPlayerMP playerMP, long oldBondId, String name, String profession, long profileId) {
        if (CommonReference.isOpped(playerMP) 
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()
                && this.bondExist(oldBondId)) {
            BoundEntityEntry entry = this.getBond(oldBondId);
            this.removeBoundEntityEntry(oldBondId);
            entry.setId(oldBondId + 1L);
            entry.setName(name);
            entry.setProfession(profession);
            entry.setProfileId(profileId);
            this.addBoundEntityEntry(entry);

            OxygenHelperServer.savePersistentDataDelegated(this);
        }
    }

    public void visitEntity(EntityPlayerMP playerMP, long bondId) {
        if (CommonReference.isOpped(playerMP)
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()
                && this.bondExist(bondId)) {
            BoundEntityEntry entry = this.getBond(bondId);
            CommonReference.teleportPlayer(playerMP, entry.dimId, entry.xPos, entry.yPos, entry.zPos);
        }
    }

    public void removeBond(EntityPlayerMP playerMP, long bondId) {
        if (CommonReference.isOpped(playerMP)
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()
                && this.bondExist(bondId)) {
            this.removeBoundEntityEntry(bondId);
            OxygenHelperServer.savePersistentDataDelegated(this);
        }
    }

    public void entityLivingDied(UUID entityUUID) {
        if (this.bondExist(entityUUID)) {
            BoundEntityEntry entry = this.getBond(entityUUID);
            long oldBondId = entry.getId();
            this.removeBoundEntityEntry(oldBondId);
            entry.setId(oldBondId + 1L);
            entry.markDead();
            this.addBoundEntityEntry(entry);

            OxygenHelperServer.savePersistentDataDelegated(this);
        }
    }

    @Override
    public String getName() {
        return "bound_entities";
    }

    @Override
    public String getModId() {
        return MerchantsMain.MODID;
    }

    @Override
    public String getPath() {
        return "world/merchants/entities.dat";
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write((short) this.entities.size(), bos);
        for (BoundEntityEntry entry : this.entities.values())
            entry.write(bos);
    }

    @Override   
    public void read(BufferedInputStream bis) throws IOException {
        int 
        amount = StreamUtils.readShort(bis),
        i = 0;
        BoundEntityEntry entityEntry;
        for (i = 0; i < amount; i++) {
            entityEntry = BoundEntityEntry.read(bis);
            this.entities.put(entityEntry.getId(), entityEntry);
            this.access.put(entityEntry.entityUUID, entityEntry.getId());
        }

        for (BoundEntityEntry entry : this.entities.values()) {
            if (!this.entriesAccess.containsKey(entry.getProfileId())) {   
                ConcurrentSetWrapper<Long> container = new ConcurrentSetWrapper();
                container.set.add(entry.getId());
                this.entriesAccess.put(entry.getProfileId(), container);
            } else
                this.entriesAccess.get(entry.getProfileId()).add(entry.getId());
        }
    }

    public void reset() {
        this.entities.clear();
        this.access.clear();
        this.entriesAccess.clear();
    }
}
