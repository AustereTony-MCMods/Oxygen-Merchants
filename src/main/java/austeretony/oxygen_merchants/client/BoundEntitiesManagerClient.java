package austeretony.oxygen_merchants.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.common.api.IPersistentData;
import austeretony.oxygen.util.ConcurrentSetWrapper;
import austeretony.oxygen.util.StreamUtils;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.server.SPCreateBond;
import austeretony.oxygen_merchants.common.network.server.SPEditBond;
import austeretony.oxygen_merchants.common.network.server.SPRemoveBond;
import austeretony.oxygen_merchants.common.network.server.SPVisitEntity;
import net.minecraft.entity.Entity;

public class BoundEntitiesManagerClient implements IPersistentData {

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

    public void removeBoundEntityEntry(long bondId) {
        BoundEntityEntry entry = this.entities.remove(bondId);
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

            OxygenHelperClient.savePersistentDataDelegated(this);
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

            OxygenHelperClient.savePersistentDataDelegated(this);
        }
    }

    public void createBondSynced(String name, String profession, long profileId) {
        Entity entity = MerchantsManagerClient.instance().getPointedEntity();
        if (!this.bondExist(ClientReference.getPersistentUUID(entity))) {
            BoundEntityEntry entry = new BoundEntityEntry(ClientReference.getPersistentUUID(entity), entity.dimension, (int) entity.posX, (int) entity.posY, (int) entity.posZ);
            entry.createId();
            entry.setName(name);
            entry.setProfession(profession);
            entry.setProfileId(profileId);
            this.addBoundEntityEntry(entry);

            MerchantsMain.LOGGER.info("Bond creration - CLIENT. Entity id: {}, uuid: {}.", entity.getEntityId(), entity.getPersistentID());//TODO DEBUG

            MerchantsMain.network().sendToServer(new SPCreateBond(entry.getId(), entity.getEntityId(), name, profession, profileId));

            OxygenHelperClient.savePersistentDataDelegated(this);
        }
    }

    public void editBondSynced(long oldBondId, String name, String profession, long profileId) {
        BoundEntityEntry entry = this.getBond(oldBondId);
        this.removeBoundEntityEntry(oldBondId);
        entry.setId(oldBondId + 1L);
        entry.setName(name);
        entry.setProfession(profession);
        entry.setProfileId(profileId);
        this.addBoundEntityEntry(entry);
        MerchantsMain.network().sendToServer(new SPEditBond(oldBondId, name, profession, profileId));

        OxygenHelperClient.savePersistentDataDelegated(this);
    }

    public void visitEntitySynced(long bondId) {
        MerchantsMain.network().sendToServer(new SPVisitEntity(bondId));
    }

    public void removeBondSynced(long bondId) {
        this.removeBoundEntityEntry(bondId);
        MerchantsMain.network().sendToServer(new SPRemoveBond(bondId));

        OxygenHelperClient.savePersistentDataDelegated(this);
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
        for (; i < amount; i++) {
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
