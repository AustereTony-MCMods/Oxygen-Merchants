package austeretony.oxygen_merchants.common.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import austeretony.oxygen.common.api.update.AbstractUpdateAdapter;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.itemstack.ItemStackWrapper;
import austeretony.oxygen.util.StreamUtils;
import austeretony.oxygen_merchants.common.main.MerchantOffer;
import austeretony.oxygen_merchants.common.main.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;

public class MerchantsUpdateAdapter extends AbstractUpdateAdapter {

    private boolean patched;

    @Override
    public String getModId() {
        return MerchantsMain.MODID;
    }

    @Override
    public String getVersion() {
        return "0.8.1:beta:0";
    }

    @Override
    public void apply() {
        if (!this.patched) {
            this.patched = true;
            MerchantsMain.LOGGER.info("Updating old merchants data...");
            try (Stream<Path> paths = Files.walk(Paths.get(CommonReference.getGameFolder() + "/oxygen/worlds"), 1)) {
                paths
                .filter(Files::isDirectory)
                .forEach((p)->this.checkPath(p));
            } catch (IOException exception) {
                exception.printStackTrace();
            }       
            MerchantsMain.LOGGER.info("Old merchant profiles data updated.");
        }
    }

    private void checkPath(Path path) {
        String pathStr = path.toString();
        this.process(pathStr + "/server/world/merchants/profiles.dat");
        this.process(pathStr + "/client/world/merchants/profiles.dat");
    }

    private void process(String folder) {
        if (Files.exists(Paths.get(folder))) {
            Map<Long, MerchantProfile> profiles = new HashMap<Long, MerchantProfile>();
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(folder))) {    
                int 
                profilesAmount = StreamUtils.readShort(bis),
                offersAmount;
                MerchantProfile profile;
                MerchantOffer offer;
                for (int i = 0; i < profilesAmount; i++) {
                    profile = new MerchantProfile();
                    profile.setId(StreamUtils.readLong(bis));
                    profile.setName(StreamUtils.readString(bis));
                    profile.setUseCurrency(StreamUtils.readBoolean(bis));
                    if (!profile.isUsingCurrency())
                        profile.setCurrencyStack(ItemStackWrapper.read(bis));
                    offersAmount = StreamUtils.readShort(bis);
                    for (int j = 0; j < offersAmount; j++) {                        
                        offer = new MerchantOffer(StreamUtils.readLong(bis), ItemStackWrapper.read(bis));
                        offer.setAmount(StreamUtils.readShort(bis));
                        offer.setSellingEnabled(StreamUtils.readBoolean(bis));
                        offer.setBuyCost(StreamUtils.readInt(bis));
                        offer.setSellingCost(StreamUtils.readInt(bis));  
                        profile.addOffer(offer);
                    }
                    profiles.put(profile.getId(), profile);
                }            
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(folder))) {   
                StreamUtils.write((short) profiles.size(), bos);
                for (MerchantProfile profile : profiles.values())
                    profile.write(bos);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            MerchantsMain.LOGGER.info("Processed: {}.", folder);
        }
    }
}