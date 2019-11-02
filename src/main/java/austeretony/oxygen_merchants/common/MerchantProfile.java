package austeretony.oxygen_merchants.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.persistent.PersistentEntry;
import austeretony.oxygen_core.common.sync.SynchronizedData;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.common.util.StreamUtils;
import io.netty.buffer.ByteBuf;

public class MerchantProfile implements PersistentEntry, SynchronizedData {

    public static final int MAX_PROFILE_NAME_LENGTH = 20;

    private long profileId;

    private String name;

    private boolean useCurrency;

    private ItemStackWrapper currencyStack;

    private final Map<Long, MerchantOffer> offers = new ConcurrentHashMap<>();

    public MerchantProfile() {}

    public long getId() {       
        return this.profileId;
    }

    public void setId(long profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUsingCurrency() {
        return this.useCurrency;        
    }

    public void setUseCurrency(boolean flag) {
        this.useCurrency = flag;
    }

    public ItemStackWrapper getCurrencyStack() {
        return this.currencyStack;
    }

    public void setCurrencyStack(ItemStackWrapper currencyStack) {
        this.currencyStack = currencyStack;
    }

    public Collection<MerchantOffer> getOffers() {
        return this.offers.values();
    }

    public boolean offerExist(long offerId) {
        return this.offers.containsKey(offerId);
    }

    public int getOffersAmount() {
        return this.offers.size();
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
        StreamUtils.write(this.profileId, bos);
        StreamUtils.write(this.name, bos);
        StreamUtils.write(this.useCurrency, bos);
        if (!this.useCurrency)
            this.currencyStack.write(bos);
        StreamUtils.write((short) this.offers.size(), bos);
        for (MerchantOffer offer : this.offers.values())
            offer.write(bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        this.profileId = StreamUtils.readLong(bis);
        this.name = StreamUtils.readString(bis);
        this.useCurrency = StreamUtils.readBoolean(bis);
        if (!this.useCurrency)
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
        buffer.writeLong(this.profileId);
        ByteBufUtils.writeString(this.name, buffer);
        buffer.writeBoolean(this.useCurrency);
        if (!this.useCurrency)
            this.currencyStack.write(buffer);
        buffer.writeShort(this.offers.size());
        for (MerchantOffer offer : this.offers.values())
            offer.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.profileId = buffer.readLong();
        this.name = ByteBufUtils.readString(buffer);
        this.useCurrency = buffer.readBoolean();
        if (!this.useCurrency)
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
        profile.profileId = this.profileId;
        profile.name = this.name;
        profile.useCurrency = this.useCurrency;
        if (!profile.useCurrency)
            profile.currencyStack = this.currencyStack.copy();
        for (MerchantOffer offer :  this.getOffers())
            profile.offers.put(offer.offerId, offer.copy());
        return profile;
    }
}
