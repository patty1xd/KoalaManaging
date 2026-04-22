package com.koala.managing.managers;

import com.koala.managing.KoalaManaging;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WarnManager {

    private final KoalaManaging plugin;
    private final File file;
    private FileConfiguration data;

    private final Map<UUID, List<String>> warnings = new HashMap<>();

    public WarnManager(KoalaManaging plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "warnings.yml");
        load();
    }

    private void load() {
        if (!file.exists()) {
            try { file.getParentFile().mkdirs(); file.createNewFile(); }
            catch (IOException e) { e.printStackTrace(); }
        }
        data = YamlConfiguration.loadConfiguration(file);
        if (data.contains("warnings")) {
            for (String uuidStr : data.getConfigurationSection("warnings").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                warnings.put(uuid, new ArrayList<>(data.getStringList("warnings." + uuidStr)));
            }
        }
    }

    public void save() {
        for (Map.Entry<UUID, List<String>> entry : warnings.entrySet())
            data.set("warnings." + entry.getKey(), entry.getValue());
        try { data.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public void addWarning(UUID uuid, String reason) {
        warnings.computeIfAbsent(uuid, k -> new ArrayList<>()).add(reason); save();
    }

    public List<String> getWarnings(UUID uuid) { return warnings.getOrDefault(uuid, Collections.emptyList()); }
    public int getWarningCount(UUID uuid) { return getWarnings(uuid).size(); }
    public void clearWarnings(UUID uuid) { warnings.remove(uuid); save(); }
}
