package com.koala.managing.listeners;

import com.koala.managing.KoalaManaging;
import com.koala.managing.managers.MuteManager;
import com.koala.managing.util.DurationParser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatMuteListener implements Listener {

    private final MuteManager muteManager;
    private final KoalaManaging plugin;

    public ChatMuteListener(KoalaManaging plugin) {
        this.plugin = plugin;
        this.muteManager = plugin.getMuteManager();
    }

    // LOWEST priority — runs first, cancels muted players before filter or FFA ever sees the event
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (muteManager.isMuted(player.getUniqueId())) {
            event.setCancelled(true);
            long remaining = muteManager.getExpiry(player.getUniqueId()) - System.currentTimeMillis();
            player.sendMessage(plugin.prefix() + "§cYou are muted for " +
                    DurationParser.format(remaining) + ". Reason: " +
                    muteManager.getReason(player.getUniqueId()));
        }
    }
}
