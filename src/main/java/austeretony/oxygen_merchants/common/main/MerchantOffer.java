package austeretony.oxygen_merchants.common.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import austeretony.oxygen.common.itemstack.ItemStackWrapper;
import austeretony.oxygen.util.StreamUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;

public class MerchantOffer {

    public final long offerId;

    private ItemStackWrapper offeredStack;

    private int 
    amount, 
    buyCost,//if buying from merchant 
    sellingCost;//if selling to merchant

    private boolean sellingEnabled;

    public MerchantOffer(long offerId, ItemStackWrapper offeredStack) {
        this.offerId = offerId;
        this.offeredStack = offeredStack;
    }

    public void setItemStack(ItemStackWrapper offeredStack) {
        this.offeredStack = offeredStack;
    }

    public ItemStackWrapper getOfferedStack() {
        return this.offeredStack;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return this.amount;
    }

    public boolean isSellingEnabled() {
        return this.sellingEnabled;
    }

    public void setSellingEnabled(boolean flag) {
        this.sellingEnabled = flag;
    }

    public int getBuyCost() {
        return this.buyCost;
    }

    public void setBuyCost(int value) {
        this.buyCost = MathHelper.clamp(value, 0, Integer.MAX_VALUE);
    }

    public int getSellingCost() {
        return this.sellingCost;
    }

    public void setSellingCost(int value) {
        this.sellingCost = MathHelper.clamp(value, 0, Integer.MAX_VALUE);
    }

    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.offerId, bos);
        this.offeredStack.write(bos);
        StreamUtils.write((short) this.amount, bos);
        StreamUtils.write(this.sellingEnabled, bos);
        StreamUtils.write(this.buyCost, bos);
        StreamUtils.write(this.sellingCost, bos);
    }

    public static MerchantOffer read(BufferedInputStream bis) throws IOException {
        MerchantOffer offer = new MerchantOffer(StreamUtils.readLong(bis), ItemStackWrapper.read(bis));
        offer.amount = StreamUtils.readShort(bis);
        offer.sellingEnabled = StreamUtils.readBoolean(bis);
        offer.buyCost = StreamUtils.readInt(bis);
        offer.sellingCost = StreamUtils.readInt(bis);
        return offer;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeLong(this.offerId);
        this.offeredStack.write(buffer);
        buffer.writeShort((short) this.amount);
        buffer.writeBoolean(this.sellingEnabled);
        buffer.writeInt(this.buyCost);
        buffer.writeInt(this.sellingCost);
    }

    public static MerchantOffer read(PacketBuffer buffer) {
        MerchantOffer offer = new MerchantOffer(buffer.readLong(), ItemStackWrapper.read(buffer));
        offer.amount = buffer.readShort();
        offer.sellingEnabled = buffer.readBoolean();      
        offer.buyCost = buffer.readInt();
        offer.sellingCost = buffer.readInt();
        return offer;
    }

    public MerchantOffer copy() {
        MerchantOffer offer = new MerchantOffer(this.offerId, this.offeredStack.copy());
        offer.amount = this.amount;
        offer.sellingEnabled = this.sellingEnabled;      
        offer.buyCost = this.buyCost;
        offer.sellingCost = this.sellingCost;
        return offer;
    }
}
