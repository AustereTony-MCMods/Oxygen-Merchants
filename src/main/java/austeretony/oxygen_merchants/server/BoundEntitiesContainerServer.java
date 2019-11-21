package austeretony.oxygen_merchants.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen_core.common.persistent.AbstractPersistentData;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import austeretony.oxygen_merchants.common.config.MerchantsConfig;

public class BoundEntitiesContainerServer extends AbstractPersistentData {

    // <bondId, BoundEntityEntry>
    private final Map<Long, BoundEntityEntry> entities = new ConcurrentHashMap<>();

    // <entityUUID, bondId>
    private final Map<UUID, Long> access = new ConcurrentHashMap<>();

    public int getEntriesAmount() {
        return this.entities.size();
    }

    public Set<Long> getEntriesIds() {
        return this.entities.keySet();
    }

    public Collection<BoundEntityEntry> getEntries() {
        return this.entities.values();
    }

    public boolean entryExist(long bondId) {
        return this.entities.containsKey(bondId);
    }

    public boolean entryExist(UUID entityUUID) {
        return this.access.containsKey(entityUUID);
    }

    public BoundEntityEntry getBoundEntityEntry(long bondId) {
        return this.entities.get(bondId);
    }

    public BoundEntityEntry getBoundEntityEntry(UUID entityUUID) {
        return this.entities.get(this.access.get(entityUUID));
    }

    public UUID getEntityUUIDById(long bondId) {
        return this.entities.get(bondId).getEntityUUID();
    }

    public void addEntry(BoundEntityEntry entry) {
        this.entities.put(entry.getId(), entry);
        this.access.put(entry.getEntityUUID(), entry.getId());
    }

    public void removeEntry(long bondId) {
        if (this.entryExist(bondId))
            this.access.remove(this.entities.remove(bondId).getEntityUUID());
    }

    public void removeAccess(UUID entityUUID) {
        this.access.remove(entityUUID);
    }

    public long createId(long seed) {
        while (this.entities.containsKey(seed))
            seed++;
        return seed;
    }

    @Override
    public String getDisplayName() {
        return "merchants:bound_entities";
    }

    @Override
    public String getPath() {
        return OxygenHelperServer.getDataFolder() + "/server/world/merchants/entities.dat";
    }

    @Override
    public long getSaveDelayMinutes() {
        return MerchantsConfig.DATA_SAVE_DELAY_MINUTES.getIntValue();
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
            entityEntry = new BoundEntityEntry();
            entityEntry.read(bis);
            this.entities.put(entityEntry.getId(), entityEntry);
            this.access.put(entityEntry.getEntityUUID(), entityEntry.getId());
        }
    }

    @Override
    public void reset() {
        this.entities.clear();
        this.access.clear();
    }
}
