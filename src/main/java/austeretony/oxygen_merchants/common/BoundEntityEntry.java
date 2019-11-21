package austeretony.oxygen_merchants.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.UUID;

import austeretony.oxygen_core.common.persistent.PersistentEntry;
import austeretony.oxygen_core.common.sync.SynchronizedData;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.common.util.StreamUtils;
import io.netty.buffer.ByteBuf;

public class BoundEntityEntry implements PersistentEntry, SynchronizedData {

    public static final int 
    MAX_NAME_LENGTH = 20,
    MAX_PROFESSION_LENGTH = 30;

    private long bondId, profileId;

    private UUID entityUUID;

    private int dimId, xPos, yPos, zPos;

    private String name, profession;

    private boolean dead;

    public BoundEntityEntry() {}

    public BoundEntityEntry(UUID entityUUID, int dimId, int xPos, int yPos, int zPos) {
        this.entityUUID = entityUUID;
        this.dimId = dimId;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public long getId() {
        return this.bondId;
    }

    public void setId(long bondId) {
        this.bondId = bondId;
    }

    public UUID getEntityUUID() {
        return this.entityUUID;
    }

    public int getDimension() {
        return this.dimId;
    }

    public int getXPos() {
        return this.xPos;
    }

    public int getYPos() {
        return this.yPos;
    }

    public int getZPos() {
        return this.zPos;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getProfession() {
        return this.profession;
    }

    public void setProfession(String str) {
        this.profession = str;
    }

    public long getMerchantProfileId() {
        return this.profileId;
    }

    public void setMerchantProfileId(long value) {
        this.profileId = value;
    }

    public boolean isDead() {
        return this.dead;
    }

    public void markDead() {
        this.dead = true;
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.entityUUID, bos);
        StreamUtils.write(this.dimId, bos);
        StreamUtils.write(this.xPos, bos);
        StreamUtils.write(this.yPos, bos);
        StreamUtils.write(this.zPos, bos);
        StreamUtils.write(this.bondId, bos);
        StreamUtils.write(this.name, bos);
        StreamUtils.write(this.profession, bos);
        StreamUtils.write(this.profileId, bos);
        StreamUtils.write(this.dead, bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        this.entityUUID = StreamUtils.readUUID(bis);
        this.dimId = StreamUtils.readInt(bis);
        this.xPos = StreamUtils.readInt(bis);
        this.yPos = StreamUtils.readInt(bis);
        this.zPos = StreamUtils.readInt(bis);
        this.bondId = StreamUtils.readLong(bis);
        this.name = StreamUtils.readString(bis);
        this.profession = StreamUtils.readString(bis);
        this.profileId = StreamUtils.readLong(bis);
        this.dead = StreamUtils.readBoolean(bis);
    }

    @Override
    public void write(ByteBuf buffer) {
        ByteBufUtils.writeUUID(this.entityUUID, buffer);
        buffer.writeLong(this.bondId);
        ByteBufUtils.writeString(this.name, buffer);
        ByteBufUtils.writeString(this.profession, buffer);
        buffer.writeLong(this.profileId);
        buffer.writeBoolean(this.dead);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.entityUUID = ByteBufUtils.readUUID(buffer);
        this.bondId = buffer.readLong();   
        this.name = ByteBufUtils.readString(buffer);
        this.profession = ByteBufUtils.readString(buffer);
        this.profileId = buffer.readLong();   
        this.dead = buffer.readBoolean();
    }

    public BoundEntityEntry copy() {
        BoundEntityEntry entry = new BoundEntityEntry(this.entityUUID, this.dimId, this.xPos, this.yPos, this.zPos);
        entry.bondId = this.bondId;   
        entry.name = this.name;
        entry.profession = this.profession;
        entry.profileId = this.profileId;   
        entry.dead = this.dead;
        return entry;
    }
}
