package austeretony.oxygen_merchants.common.merchant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.persistent.PersistentEntry;
import austeretony.oxygen_core.common.sync.SynchronousEntry;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.common.util.StreamUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MerchantProfile implements PersistentEntry, SynchronousEntry {

    public static final int MAX_PROFILE_NAME_LENGTH = 24;

    private long persistentId, versionId;

    private String fileName, displayName;

    private int currencyIndex;

    private ItemStackWrapper currencyStackWrapper = ItemStackWrapper.of(new ItemStack(Items.AIR));

    private final Map<Long, MerchantOffer> offers = new ConcurrentHashMap<>();

    public MerchantProfile() {}

    public MerchantProfile(long persistentiId, String fileName, String displayName) {
        this.persistentId = persistentiId;
        this.fileName = fileName;
        this.displayName = displayName;
    }

    public long getId() {       
        return this.versionId;
    }

    public void setId(long profileId) {
        this.versionId = profileId;
    }

    public long getPersistentId() {       
        return this.persistentId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public boolean isUsingVirtalCurrency() {
        return this.currencyIndex != - 1;        
    }

    public int getCurrencyIndex() {
        return this.currencyIndex;        
    }

    public void setCurrencyIndex(int currencyIndex) {
        this.currencyIndex = currencyIndex;
    }

    @Nonnull
    public ItemStackWrapper getCurrencyStackWrapper() {
        return this.currencyStackWrapper;
    }

    public void setCurrencyStack(ItemStackWrapper currencyStack) {
        this.currencyStackWrapper = currencyStack;
        this.currencyIndex = - 1;
    }

    public Collection<MerchantOffer> getOffers() {
        return this.offers.values();
    }

    public int getOffersAmount() {
        return this.offers.size();
    }

    public int getBuyOffersAmount() {
        int amount = 0;
        for (MerchantOffer offer : this.getOffers())
            if (offer.isBuyEnabled())
                amount++;
        return amount;
    }

    public int getSellingOffersAmount() {
        int amount = 0;
        for (MerchantOffer offer : this.getOffers())
            if (offer.isSellingEnabled())
                amount++;
        return amount;
    }

    @Nullable
    public MerchantOffer getOffer(long offerId) {
        return this.offers.get(offerId);
    }

    public void addOffer(MerchantOffer offer) {
        this.offers.put(offer.getId(), offer);
    }

    public void removeOffer(long offerId) {
        this.offers.remove(offerId);
    }

    public static MerchantProfile fromJson(JsonObject jsonObject) {
        MerchantProfile profile = new MerchantProfile();
        profile.persistentId = jsonObject.get("persistent_id").getAsLong();
        profile.versionId = jsonObject.get("version_id").getAsLong();
        profile.displayName = jsonObject.get("display_name").getAsString();
        profile.currencyIndex = jsonObject.get("currency_index").getAsInt();
        profile.currencyStackWrapper = ItemStackWrapper.fromJson(jsonObject.get("currency_itemstack").getAsJsonObject());

        JsonArray offersArray = jsonObject.get("offers").getAsJsonArray();
        for (JsonElement element : offersArray)
            profile.addOffer(MerchantOffer.fromJson(element.getAsJsonObject()));

        return profile;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("persistent_id", new JsonPrimitive(this.persistentId));
        jsonObject.add("version_id", new JsonPrimitive(this.versionId));
        jsonObject.add("display_name", new JsonPrimitive(this.displayName));
        jsonObject.add("currency_index", new JsonPrimitive(this.currencyIndex));
        jsonObject.add("currency_itemstack", this.currencyStackWrapper.toJson());

        JsonArray offersArray = new JsonArray();            
        this.getOffers().stream()
        .sorted((o1, o2)->o1.getId() < o2.getId() ? - 1 : o1.getId() > o2.getId() ? 1 : 0)
        .forEach((offer)->offersArray.add(offer.toJson()));   
        jsonObject.add("offers", offersArray);

        return jsonObject;
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.persistentId, bos);
        StreamUtils.write(this.versionId, bos);
        StreamUtils.write(this.displayName, bos);
        StreamUtils.write(this.fileName, bos);

        StreamUtils.write((byte) this.currencyIndex, bos);
        this.currencyStackWrapper.write(bos);

        StreamUtils.write((short) this.offers.size(), bos);
        for (MerchantOffer offer : this.offers.values())
            offer.write(bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        this.persistentId = StreamUtils.readLong(bis);
        this.versionId = StreamUtils.readLong(bis);
        this.displayName = StreamUtils.readString(bis);
        this.fileName = StreamUtils.readString(bis);

        this.currencyIndex = StreamUtils.readByte(bis);
        this.currencyStackWrapper = ItemStackWrapper.read(bis);

        int amount = StreamUtils.readShort(bis);
        for (int i = 0; i < amount; i++)
            this.addOffer(MerchantOffer.read(bis));
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeLong(this.persistentId);
        buffer.writeLong(this.versionId);
        ByteBufUtils.writeString(this.displayName, buffer);
        ByteBufUtils.writeString(this.fileName, buffer);

        buffer.writeByte(this.currencyIndex);
        this.currencyStackWrapper.write(buffer);

        buffer.writeShort(this.offers.size());
        for (MerchantOffer offer : this.offers.values())
            offer.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.persistentId = buffer.readLong();
        this.versionId = buffer.readLong();
        this.displayName = ByteBufUtils.readString(buffer);
        this.fileName = ByteBufUtils.readString(buffer);

        this.currencyIndex = buffer.readByte();
        this.currencyStackWrapper = ItemStackWrapper.read(buffer);

        int amount = buffer.readShort();
        for (int i = 0; i < amount; i++)
            this.addOffer(MerchantOffer.read(buffer));
    }

    public MerchantProfile copy() {
        MerchantProfile profile = new MerchantProfile();
        profile.persistentId = this.persistentId;
        profile.versionId = this.versionId;
        profile.displayName = this.displayName;
        profile.fileName = this.fileName;

        profile.currencyIndex = this.currencyIndex;
        profile.currencyStackWrapper = this.currencyStackWrapper.copy();

        for (MerchantOffer offer :  this.getOffers())
            this.addOffer(offer.copy());

        return profile;
    }
}
