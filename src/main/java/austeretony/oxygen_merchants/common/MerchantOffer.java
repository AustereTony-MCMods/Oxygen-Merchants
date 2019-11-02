package austeretony.oxygen_merchants.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_core.common.util.StreamUtils;
import io.netty.buffer.ByteBuf;

public class MerchantOffer {

    public final long offerId;

    private ItemStackWrapper offeredStack;

    private int amount;

    private long 
    buyCost,//if buying from merchant 
    sellingCost;//if selling to merchant

    private boolean sellingEnabled, sellingOnly;

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

    public boolean isSellingOnly() {
        return this.sellingOnly;
    }

    public void setSellingOnly(boolean flag) {
        this.sellingOnly = flag;
    }

    public long getBuyCost() {
        return this.buyCost;
    }

    public void setBuyCost(long value) {
        this.buyCost = MathUtils.clamp(value, 0, Long.MAX_VALUE);
    }

    public long getSellingCost() {
        return this.sellingCost;
    }

    public void setSellingCost(long value) {
        this.sellingCost = MathUtils.clamp(value, 0, Long.MAX_VALUE);
    }

    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.offerId, bos);
        this.offeredStack.write(bos);
        StreamUtils.write((short) this.amount, bos);
        StreamUtils.write(this.sellingEnabled, bos);
        StreamUtils.write(this.sellingOnly, bos);
        StreamUtils.write(this.buyCost, bos);
        StreamUtils.write(this.sellingCost, bos);
    }

    public static MerchantOffer read(BufferedInputStream bis) throws IOException {
        MerchantOffer offer = new MerchantOffer(StreamUtils.readLong(bis), ItemStackWrapper.read(bis));
        offer.amount = StreamUtils.readShort(bis);
        offer.sellingEnabled = StreamUtils.readBoolean(bis);
        offer.sellingOnly = StreamUtils.readBoolean(bis);
        offer.buyCost = StreamUtils.readLong(bis);
        offer.sellingCost = StreamUtils.readLong(bis);
        return offer;
    }

    public void write(ByteBuf buffer) {
        buffer.writeLong(this.offerId);
        this.offeredStack.write(buffer);
        buffer.writeShort((short) this.amount);
        buffer.writeBoolean(this.sellingEnabled);
        buffer.writeBoolean(this.sellingOnly);
        buffer.writeLong(this.buyCost);
        buffer.writeLong(this.sellingCost);
    }

    public static MerchantOffer read(ByteBuf buffer) {
        MerchantOffer offer = new MerchantOffer(buffer.readLong(), ItemStackWrapper.read(buffer));
        offer.amount = buffer.readShort();
        offer.sellingEnabled = buffer.readBoolean();      
        offer.sellingOnly = buffer.readBoolean();      
        offer.buyCost = buffer.readLong();
        offer.sellingCost = buffer.readLong();
        return offer;
    }

    public MerchantOffer copy() {
        MerchantOffer offer = new MerchantOffer(this.offerId, this.offeredStack.copy());
        offer.amount = this.amount;
        offer.sellingEnabled = this.sellingEnabled;    
        offer.sellingOnly = this.sellingOnly;      
        offer.buyCost = this.buyCost;
        offer.sellingCost = this.sellingCost;
        return offer;
    }
}
