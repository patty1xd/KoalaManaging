package com.koala.managing;

import com.koala.managing.commands.*;
import com.koala.managing.listeners.FreezeListener;
import com.koala.managing.managers.BanManager;
import com.koala.managing.managers.MuteManager;
import com.koala.managing.managers.FreezeManager;
import com.koala.managing.managers.WarnManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class KoalaManaging extends JavaPlugin {

    private static KoalaManaging instance;
    private BanManager banManager;
    private MuteManager muteManager;
    private FreezeManager freezeManager;
    private WarnManager warnManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        banManager = new BanManager(this);
        muteManager = new MuteManager(this);
        freezeManager = new FreezeManager(this);
        warnManager = new WarnManager(this);

        registerCommands();

        getServer().getPluginManager().registerEvents(new FreezeListener(freezeManager), this);
        getServer().getPluginManager().registerEvents(new com.koala.managing.listeners.ChatMuteListener(this), this);

        getLogger().info("KoalaManaging enabled successfully!");
    }

    @Override
    public void onDisable() {
        banManager.save();
        muteManager.save();
        warnManager.save();
        getLogger().info("KoalaManaging disabled.");
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("tempban")).setExecutor(new TempBanCommand(this));
        Objects.requireNonNull(getCommand("tempban")).setTabCompleter(new TempBanCommand(this));
        Objects.requireNonNull(getCommand("tempmute")).setExecutor(new TempMuteCommand(this));
        Objects.requireNonNull(getCommand("tempmute")).setTabCompleter(new TempMuteCommand(this));
        Objects.requireNonNull(getCommand("unban")).setExecutor(new UnbanCommand(this));
        Objects.requireNonNull(getCommand("unban")).setTabCompleter(new UnbanCommand(this));
        Objects.requireNonNull(getCommand("unmute")).setExecutor(new UnmuteCommand(this));
        Objects.requireNonNull(getCommand("unmute")).setTabCompleter(new UnmuteCommand(this));
        Objects.requireNonNull(getCommand("freeze")).setExecutor(new FreezeCommand(this));
        Objects.requireNonNull(getCommand("freeze")).setTabCompleter(new FreezeCommand(this));
        Objects.requireNonNull(getCommand("unfreeze")).setExecutor(new UnfreezeCommand(this));
        Objects.requireNonNull(getCommand("unfreeze")).setTabCompleter(new UnfreezeCommand(this));
        Objects.requireNonNull(getCommand("broadcast")).setExecutor(new BroadcastCommand(this));
        Objects.requireNonNull(getCommand("echest")).setExecutor(new EChestCommand(this));
        Objects.requireNonNull(getCommand("echest")).setTabCompleter(new EChestCommand(this));
        Objects.requireNonNull(getCommand("inventory")).setExecutor(new InventoryCommand(this));
        Objects.requireNonNull(getCommand("inventory")).setTabCompleter(new InventoryCommand(this));
        Objects.requireNonNull(getCommand("kick")).setExecutor(new KickCommand(this));
        Objects.requireNonNull(getCommand("kick")).setTabCompleter(new KickCommand(this));
        Objects.requireNonNull(getCommand("warn")).setExecutor(new WarnCommand(this));
        Objects.requireNonNull(getCommand("warn")).setTabCompleter(new WarnCommand(this));
        Objects.requireNonNull(getCommand("warnings")).setExecutor(new WarningsCommand(this));
        Objects.requireNonNull(getCommand("warnings")).setTabCompleter(new WarningsCommand(this));
    }

    public static KoalaManaging getInstance() { return instance; }
    public BanManager getBanManager() { return banManager; }
    public MuteManager getMuteManager() { return muteManager; }
    public FreezeManager getFreezeManager() { return freezeManager; }
    public WarnManager getWarnManager() { return warnManager; }

    public String prefix() {
        return colorize(getConfig().getString("messages.prefix", "&8[&bKoala&8] &r"));
    }

    public String colorize(String s) {
        return s.replace("&", "\u00a7");
    }
}
