package com.koala.managing.listeners;

import com.koala.managing.KoalaManaging;
import com.koala.managing.managers.BanManager;
import com.koala.managing.util.DurationParser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class BanLoginListener implements Listener {

    private final KoalaManaging plugin;

    public BanLoginListener(KoalaManaging plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        BanManager bm = plugin.getBanManager();

        if (!bm.isBanned(event.getPlayer().getUniqueId())) return;

        long expiry = bm.getExpiry(event.getPlayer().getUniqueId());
        long remaining = expiry - System.currentTimeMillis();
        String reason = bm.getReason(event.getPlayer().getUniqueId());

        String message = plugin.colorize(
            "&cYou are temporarily banned.\n" +
            "&7Reason: &f" + reason + "\n" +
            "&7Time remaining: &f" + DurationParser.format(remaining)
        );

        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
    }
}
