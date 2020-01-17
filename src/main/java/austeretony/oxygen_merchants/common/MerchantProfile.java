package austeretony.oxygen_merchants.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.persistent.PersistentEntry;
import austeretony.oxygen_core.common.sync.SynchronousEntry;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.common.util.StreamUtils;
import io.netty.buffer.ByteBuf;

public class MerchantProfile implements PersistentEntry, SynchronousEntry {

    public static final int MAX_PROFILE_NAME_LENGTH = 24;

    private long persistentId, profileId;

    private String name;

    private int currencyIndex;

    private ItemStackWrapper currencyStack;

    private final Map<Long, MerchantOffer> offers = new ConcurrentHashMap<>();

    public MerchantProfile() {}

    public long getId() {       
        return this.profileId;
    }

    public void setId(long profileId) {
        this.profileId = profileId;
    }

    public long getPersistentId() {       
        return this.persistentId;
    }

    public void setPersistentId(long persistentId) {
        this.persistentId = persistentId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUsingVirtalCurrency() {
        return this.currencyIndex != - 1;        
    }

    public int getCurrencyIndex() {
        return this.currencyIndex;        
    }

    public void setCurrencyIndex(int currencyIndex) {
        this.currencyIndex = currencyIndex;
        this.currencyStack = null;
    }

    public ItemStackWrapper getCurrencyStack() {
        return this.currencyStack;
    }

    public void setCurrencyStack(ItemStackWrapper currencyStack) {
        this.currencyStack = currencyStack;
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

    public MerchantOffer getOffer(long offerId) {
        return this.offers.get(offerId);
    }

    public void addOffer(MerchantOffer offer) {
        this.offers.put(offer.offerId, offer);
    }

    public void removeOffer(long offerId) {
        this.offers.remove(offerId);
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.persistentId, bos);
        StreamUtils.write(this.profileId, bos);
        StreamUtils.write(this.name, bos);
        StreamUtils.write((byte) this.currencyIndex, bos);
        if (this.currencyIndex == - 1)
            this.currencyStack.write(bos);
        StreamUtils.write((short) this.offers.size(), bos);
        for (MerchantOffer offer : this.offers.values())
            offer.write(bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        this.persistentId = StreamUtils.readLong(bis);
        this.profileId = StreamUtils.readLong(bis);
        this.name = StreamUtils.readString(bis);
        this.currencyIndex = StreamUtils.readByte(bis);
        if (this.currencyIndex == - 1)
            this.currencyStack = ItemStackWrapper.read(bis);
        int amount = StreamUtils.readShort(bis);
        MerchantOffer offer;
        for (int i = 0; i < amount; i++) {
            offer = MerchantOffer.read(bis);
            this.offers.put(offer.offerId, offer);
        }
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeLong(this.persistentId);
        buffer.writeLong(this.profileId);
        ByteBufUtils.writeString(this.name, buffer);
        buffer.writeByte(this.currencyIndex);
        if (this.currencyIndex == - 1)
            this.currencyStack.write(buffer);
        buffer.writeShort(this.offers.size());
        for (MerchantOffer offer : this.offers.values())
            offer.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.persistentId = buffer.readLong();
        this.profileId = buffer.readLong();
        this.name = ByteBufUtils.readString(buffer);
        this.currencyIndex = buffer.readByte();
        if (this.currencyIndex == - 1)
            this.currencyStack = ItemStackWrapper.read(buffer);
        int amount = buffer.readShort();
        MerchantOffer offer;
        for (int i = 0; i < amount; i++) {
            offer = MerchantOffer.read(buffer);
            this.offers.put(offer.offerId, offer);
        }
    }

    public MerchantProfile copy() {
        MerchantProfile profile = new MerchantProfile();
        profile.persistentId = this.persistentId;
        profile.profileId = this.profileId;
        profile.name = this.name;
        profile.currencyIndex = this.currencyIndex;
        if (this.currencyIndex == - 1)
            profile.currencyStack = this.currencyStack.copy();
        for (MerchantOffer offer :  this.getOffers())
            profile.offers.put(offer.offerId, offer.copy());
        return profile;
    }
}
