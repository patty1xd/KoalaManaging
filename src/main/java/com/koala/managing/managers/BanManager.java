package com.koala.managing.managers;

import com.koala.managing.KoalaManaging;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BanManager {

    private final KoalaManaging plugin;
    private final File file;
    private FileConfiguration data;

    private final Map<UUID, Long> bans = new HashMap<>();
    private final Map<UUID, String> reasons = new HashMap<>();
    private final Map<UUID, String> names = new HashMap<>();

    public BanManager(KoalaManaging plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "bans.yml");
        load();
    }

    private void load() {
        if (!file.exists()) {
            try { file.getParentFile().mkdirs(); file.createNewFile(); }
            catch (IOException e) { e.printStackTrace(); }
        }
        data = YamlConfiguration.loadConfiguration(file);
        if (data.contains("bans")) {
            for (String uuidStr : data.getConfigurationSection("bans").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                bans.put(uuid, data.getLong("bans." + uuidStr + ".expiry"));
                reasons.put(uuid, data.getString("bans." + uuidStr + ".reason", "No reason given"));
                names.put(uuid, data.getString("bans." + uuidStr + ".name", uuidStr));
            }
        }
    }

    public void save() {
        for (Map.Entry<UUID, Long> entry : bans.entrySet()) {
            String path = "bans." + entry.getKey();
            data.set(path + ".expiry", entry.getValue());
            data.set(path + ".reason", reasons.getOrDefault(entry.getKey(), "No reason given"));
            data.set(path + ".name", names.getOrDefault(entry.getKey(), entry.getKey().toString()));
        }
        try { data.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public void addBan(UUID uuid, String name, long durationMillis, String reason) {
        bans.put(uuid, System.currentTimeMillis() + durationMillis);
        reasons.put(uuid, reason);
        names.put(uuid, name);
        save();
    }

    public void removeBan(UUID uuid) {
        bans.remove(uuid); reasons.remove(uuid); names.remove(uuid); save();
    }

    public boolean isBanned(UUID uuid) {
        if (!bans.containsKey(uuid)) return false;
        long expiry = bans.get(uuid);
        if (expiry > 0 && System.currentTimeMillis() > expiry) { removeBan(uuid); return false; }
        return true;
    }

    public long getExpiry(UUID uuid) { return bans.getOrDefault(uuid, 0L); }
    public String getReason(UUID uuid) { return reasons.getOrDefault(uuid, "No reason given"); }
    public Map<UUID, Long> getBans() { return bans; }
    public Map<UUID, String> getNames() { return names; }
}
