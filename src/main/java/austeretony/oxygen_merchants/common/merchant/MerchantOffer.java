package austeretony.oxygen_merchants.common.merchant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_core.common.util.StreamUtils;
import io.netty.buffer.ByteBuf;

public class MerchantOffer {

    private final long id;

    private ItemStackWrapper stackWrapper;

    private int amount;

    private long 
    buyCost,//if buying from merchant 
    sellingCost;//if selling to merchant

    public MerchantOffer(long offerId, ItemStackWrapper offeredStack, int amount, long buyCost, long sellingCost) {
        this.id = offerId;
        this.stackWrapper = offeredStack;
        this.amount = amount;
        this.buyCost = buyCost;
        this.sellingCost = sellingCost;
    }

    public long getId() {
        return this.id;
    }

    public ItemStackWrapper getStackWrapper() {
        return this.stackWrapper;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return this.amount;
    }

    public boolean isBuyEnabled() {
        return this.buyCost != 0L;
    }

    public long getBuyCost() {
        return this.buyCost;
    }

    public void setBuyCost(long value) {
        this.buyCost = MathUtils.clamp(value, 0L, Long.MAX_VALUE);
    }

    public boolean isSellingEnabled() {
        return this.sellingCost != 0L;
    }

    public long getSellingCost() {
        return this.sellingCost;
    }

    public void setSellingCost(long value) {
        this.sellingCost = MathUtils.clamp(value, 0L, Long.MAX_VALUE);
    }

    @Override
    public String toString() {
        return String.format("[id: %d, itemstack: %s, amount: %d, buy cost: %d, selling cost: %d]", 
                this.id,
                this.stackWrapper,
                this.amount,
                this.buyCost,
                this.sellingCost);
    }

    public static MerchantOffer fromJson(JsonObject jsonObject) {
        MerchantOffer offer = new MerchantOffer(
                jsonObject.get("id").getAsLong(),
                ItemStackWrapper.fromJson(jsonObject.get("itemstack").getAsJsonObject()),
                jsonObject.get("amount").getAsInt(),
                jsonObject.get("buy_cost").getAsLong(),
                jsonObject.get("selling_cost").getAsLong());

        return offer;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("id", new JsonPrimitive(this.id));
        jsonObject.add("amount", new JsonPrimitive(this.amount));
        jsonObject.add("buy_cost", new JsonPrimitive(this.buyCost));
        jsonObject.add("selling_cost", new JsonPrimitive(this.sellingCost));
        jsonObject.add("itemstack", this.stackWrapper.toJson());

        return jsonObject;
    }

    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.id, bos);
        this.stackWrapper.write(bos);
        StreamUtils.write((short) this.amount, bos);
        StreamUtils.write(this.buyCost, bos);
        StreamUtils.write(this.sellingCost, bos);
    }

    public static MerchantOffer read(BufferedInputStream bis) throws IOException {
        MerchantOffer offer = new MerchantOffer(
                StreamUtils.readLong(bis), 
                ItemStackWrapper.read(bis),
                StreamUtils.readShort(bis),
                StreamUtils.readLong(bis),
                StreamUtils.readLong(bis));

        return offer;
    }

    public void write(ByteBuf buffer) {
        buffer.writeLong(this.id);
        this.stackWrapper.write(buffer);
        buffer.writeShort((short) this.amount);
        buffer.writeLong(this.buyCost);
        buffer.writeLong(this.sellingCost);
    }

    public static MerchantOffer read(ByteBuf buffer) {
        MerchantOffer offer = new MerchantOffer(
                buffer.readLong(), 
                ItemStackWrapper.read(buffer),
                buffer.readShort(),
                buffer.readLong(),
                buffer.readLong());

        return offer;
    }

    public MerchantOffer copy() {
        MerchantOffer offer = new MerchantOffer(
                this.id, 
                this.stackWrapper.copy(),
                this.amount,
                this.buyCost,
                this.sellingCost);

        return offer;
    }
}
