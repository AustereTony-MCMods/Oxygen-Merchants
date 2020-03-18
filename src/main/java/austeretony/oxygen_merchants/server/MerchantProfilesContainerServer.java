package austeretony.oxygen_merchants.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.util.JsonUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.merchant.MerchantProfile;

public class MerchantProfilesContainerServer {

    private final Map<Long, MerchantProfile> profiles = new ConcurrentHashMap<>();

    public int getProfilesAmount() {
        return this.profiles.size();
    }

    public Set<Long> getProfilesIds() {
        return this.profiles.keySet();
    }

    public Collection<MerchantProfile> getProfiles() {
        return this.profiles.values();
    }

    @Nullable
    public MerchantProfile getProfile(long profileId) {
        return this.profiles.get(profileId);
    }

    @Nullable
    public MerchantProfile getProfileByPersistentId(long persistentId) {
        for (MerchantProfile profile : this.profiles.values())
            if (profile.getPersistentId() == persistentId)
                return profile;
        return null;
    }

    public void addProfile(MerchantProfile profile) {
        this.profiles.put(profile.getId(), profile);
    }

    @Nullable
    public MerchantProfile removeProfile(long profileId) {
        return this.profiles.remove(profileId);
    }

    public long createId(long seed) {
        long id = seed++;
        while (this.profiles.containsKey(id))
            id++;
        return id;
    }

    public Future<?> loadAsync() {
        return OxygenHelperServer.addIOTask(this::load);
    }

    public void load() {
        this.profiles.clear();

        String folder = OxygenHelperCommon.getConfigFolder() + "data/server/merchants/";
        File file = new File(folder);
        if (file.exists()) {
            this.loadProfilesFromFolder(file);
            OxygenMain.LOGGER.info("[Merchants] Loaded {} merchant profiles.", this.profiles.size());
        }
    }

    private void loadProfilesFromFolder(File folder) {
        for (File entry : folder.listFiles()) {
            if (entry.isDirectory())
                this.loadProfilesFromFolder(entry);
            else
                if (entry.getName().endsWith(".json"))
                    this.loadProfile(entry);
        }
    }

    private void loadProfile(File file) {
        try (
                InputStream inputStream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8")) {                                       
            JsonObject profileObject = new JsonParser().parse(reader).getAsJsonObject();  
            MerchantProfile profile = MerchantProfile.fromJson(profileObject);
            profile.setFileName(file.getName());
            this.addProfile(profile);
        } catch (IOException exception) {
            OxygenMain.LOGGER.error("[Merchants] Failed to load merchant profile from file: {}", file.getName());
            exception.printStackTrace();
        }
    }

    public Future<?> saveProfileAsync(long persistentId) {
        return OxygenHelperServer.addIOTask(()->this.saveProfile(persistentId));
    }

    public void saveProfile(long persistentId) {
        MerchantProfile profile = this.getProfileByPersistentId(persistentId);
        if (profile != null) {
            String pathStr = OxygenHelperCommon.getConfigFolder() + "data/server/merchants/" + profile.getFileName();
            Path 
            path = Paths.get(pathStr),
            parentPath = path.getParent();
            try {    
                if (!Files.exists(parentPath))
                    Files.createDirectory(parentPath);
                JsonUtils.createExternalJsonFile(pathStr, profile.toJson());
            } catch (IOException exception) {
                OxygenMain.LOGGER.error("[Merchants] Failed to save merchant profile <{} ({})> to file: {}",
                        profile.getDisplayName(),
                        profile.getPersistentId(),
                        profile.getFileName());
                exception.printStackTrace();
            }
            OxygenMain.LOGGER.info("[Merchants] Merchant profile <{} ({})> saved to file: {}",
                    profile.getDisplayName(),
                    profile.getPersistentId(),
                    profile.getFileName());
        }
    }

    public Future<?> removeProfileFileAsync(long persistentId) {
        return OxygenHelperServer.addIOTask(()->this.removeProfileFile(persistentId));
    }

    public void removeProfileFile(long persistentId) {
        MerchantProfile profile = this.getProfileByPersistentId(persistentId);
        if (profile != null) {
            String pathStr = OxygenHelperCommon.getConfigFolder() + "data/server/merchants/" + profile.getFileName();
            Path path = Paths.get(pathStr);
            if (Files.exists(path)) {
                try {    
                    Files.delete(path);
                } catch (IOException exception) {
                    OxygenMain.LOGGER.error("[Merchants] Failed to remove merchant profile <{} ({})>, file: {}",
                            profile.getDisplayName(),
                            profile.getPersistentId(),
                            profile.getFileName());
                    exception.printStackTrace();
                }
                OxygenMain.LOGGER.info("[Merchants] Merchant profile <{} ({})> removed, file: {}",
                        profile.getDisplayName(),
                        profile.getPersistentId(),
                        profile.getFileName());
            }
        }
    }
}
