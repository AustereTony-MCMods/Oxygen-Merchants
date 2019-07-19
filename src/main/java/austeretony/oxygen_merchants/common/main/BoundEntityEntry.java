package austeretony.oxygen_merchants.common.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.UUID;

import austeretony.oxygen.util.OxygenUtils;
import austeretony.oxygen.util.PacketBufferUtils;
import austeretony.oxygen.util.StreamUtils;
import net.minecraft.network.PacketBuffer;

public class BoundEntityEntry {

    public static final int 
    MAX_NAME_LENGTH = 20,
    MAX_PROFESSION_LENGTH = 30;

    private long bondId, profileId;

    public final UUID entityUUID;

    public final int dimId, xPos, yPos, zPos;//used only server side for teleportation to entity

    private String name, profession;

    private boolean dead;

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

    public void createId() {
        this.bondId = OxygenUtils.createDataStampedId();
    }

    public void setId(long bondId) {
        this.bondId = bondId;
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

    public long getProfileId() {
        return this.profileId;
    }

    public void setProfileId(long value) {
        this.profileId = value;
    }

    public boolean isDead() {
        return this.dead;
    }

    public void markDead() {
        this.dead = true;
    }

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

    public static BoundEntityEntry read(BufferedInputStream bis) throws IOException {
        BoundEntityEntry entry = new BoundEntityEntry(StreamUtils.readUUID(bis), StreamUtils.readInt(bis), StreamUtils.readInt(bis), StreamUtils.readInt(bis), StreamUtils.readInt(bis));
        entry.bondId = StreamUtils.readLong(bis);
        entry.name = StreamUtils.readString(bis);
        entry.profession = StreamUtils.readString(bis);
        entry.profileId = StreamUtils.readLong(bis);
        entry.dead = StreamUtils.readBoolean(bis);
        return entry;
    }

    public void write(PacketBuffer buffer) {
        PacketBufferUtils.writeUUID(this.entityUUID, buffer);
        buffer.writeInt(this.dimId);
        buffer.writeLong(this.bondId);
        PacketBufferUtils.writeString(this.name, buffer);
        PacketBufferUtils.writeString(this.profession, buffer);
        buffer.writeLong(this.profileId);
        buffer.writeBoolean(this.dead);
    }

    public static BoundEntityEntry read(PacketBuffer buffer) {
        BoundEntityEntry entry = new BoundEntityEntry(PacketBufferUtils.readUUID(buffer), buffer.readInt(), 0, 0, 0);
        entry.bondId = buffer.readLong();   
        entry.name = PacketBufferUtils.readString(buffer);
        entry.profession = PacketBufferUtils.readString(buffer);
        entry.profileId = buffer.readLong();   
        entry.dead = buffer.readBoolean();
        return entry;
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
