package com.koala.managing.managers;

import com.koala.managing.KoalaManaging;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MuteManager {

    private final KoalaManaging plugin;
    private final File file;
    private FileConfiguration data;

    private final Map<UUID, Long> mutes = new HashMap<>();
    private final Map<UUID, String> reasons = new HashMap<>();
    private final Map<UUID, String> names = new HashMap<>();

    public MuteManager(KoalaManaging plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "mutes.yml");
        load();
    }

    private void load() {
        if (!file.exists()) {
            try { file.getParentFile().mkdirs(); file.createNewFile(); }
            catch (IOException e) { e.printStackTrace(); }
        }
        data = YamlConfiguration.loadConfiguration(file);
        if (data.contains("mutes")) {
            for (String uuidStr : data.getConfigurationSection("mutes").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                mutes.put(uuid, data.getLong("mutes." + uuidStr + ".expiry"));
                reasons.put(uuid, data.getString("mutes." + uuidStr + ".reason", "No reason given"));
                names.put(uuid, data.getString("mutes." + uuidStr + ".name", uuidStr));
            }
        }
    }

    public void save() {
        for (Map.Entry<UUID, Long> entry : mutes.entrySet()) {
            String path = "mutes." + entry.getKey();
            data.set(path + ".expiry", entry.getValue());
            data.set(path + ".reason", reasons.getOrDefault(entry.getKey(), "No reason"));
            data.set(path + ".name", names.getOrDefault(entry.getKey(), entry.getKey().toString()));
        }
        try { data.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public void addMute(UUID uuid, String name, long durationMillis, String reason) {
        mutes.put(uuid, System.currentTimeMillis() + durationMillis);
        reasons.put(uuid, reason); names.put(uuid, name); save();
    }

    public void removeMute(UUID uuid) {
        mutes.remove(uuid); reasons.remove(uuid); names.remove(uuid); save();
    }

    public boolean isMuted(UUID uuid) {
        if (!mutes.containsKey(uuid)) return false;
        if (System.currentTimeMillis() > mutes.get(uuid)) { removeMute(uuid); return false; }
        return true;
    }

    public long getExpiry(UUID uuid) { return mutes.getOrDefault(uuid, 0L); }
    public String getReason(UUID uuid) { return reasons.getOrDefault(uuid, "No reason"); }
    public Map<UUID, String> getNames() { return names; }
}
