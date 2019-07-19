package austeretony.oxygen_merchants.common.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen.common.itemstack.ItemStackWrapper;
import austeretony.oxygen.util.OxygenUtils;
import austeretony.oxygen.util.PacketBufferUtils;
import austeretony.oxygen.util.StreamUtils;
import net.minecraft.network.PacketBuffer;

public class MerchantProfile {

    public static final int MAX_PROFILE_NAME_LENGTH = 20;

    private long profileId;

    private String name;

    private boolean useCurrency;

    private ItemStackWrapper currencyStack;

    private final Map<Long, MerchantOffer> offers = new ConcurrentHashMap<Long, MerchantOffer>();

    public long getId() {       
        return this.profileId;
    }

    public void createId() {
        this.profileId = OxygenUtils.createDataStampedId();
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

    public static MerchantProfile read(BufferedInputStream bis) throws IOException {
        MerchantProfile profile = new MerchantProfile();
        profile.profileId = StreamUtils.readLong(bis);
        profile.name = StreamUtils.readString(bis);
        profile.useCurrency = StreamUtils.readBoolean(bis);
        if (!profile.useCurrency)
            profile.currencyStack = ItemStackWrapper.read(bis);
        int amount = StreamUtils.readShort(bis);
        MerchantOffer offer;
        for (int i = 0; i < amount; i++) {
            offer = MerchantOffer.read(bis);
            profile.offers.put(offer.offerId, offer);
        }
        return profile;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeLong(this.profileId);
        PacketBufferUtils.writeString(this.name, buffer);
        buffer.writeBoolean(this.useCurrency);
        if (!this.useCurrency)
            this.currencyStack.write(buffer);
        buffer.writeShort(this.offers.size());
        for (MerchantOffer offer : this.offers.values())
            offer.write(buffer);
    }

    public static MerchantProfile read(PacketBuffer buffer) {
        MerchantProfile profile = new MerchantProfile();
        profile.profileId = buffer.readLong();
        profile.name = PacketBufferUtils.readString(buffer);
        profile.useCurrency = buffer.readBoolean();
        if (!profile.useCurrency)
            profile.currencyStack = ItemStackWrapper.read(buffer);
        int amount = buffer.readShort();
        MerchantOffer offer;
        for (int i = 0; i < amount; i++) {
            offer = MerchantOffer.read(buffer);
            profile.offers.put(offer.offerId, offer);
        }
        return profile;
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
