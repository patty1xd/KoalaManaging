package com.koala.managing.listeners;

import com.koala.managing.KoalaManaging;
import com.koala.managing.managers.MuteManager;
import com.koala.managing.util.DurationParser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatMuteListener implements Listener {

    private final MuteManager muteManager;
    private final KoalaManaging plugin;

    public ChatMuteListener(KoalaManaging plugin) {
        this.plugin = plugin;
        this.muteManager = plugin.getMuteManager();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (muteManager.isMuted(player.getUniqueId())) {
            event.setCancelled(true);
            long remaining = muteManager.getExpiry(player.getUniqueId()) - System.currentTimeMillis();
            player.sendMessage(plugin.prefix() + "\u00a7cYou are muted for " +
                    DurationParser.format(remaining) + ". Reason: " +
                    muteManager.getReason(player.getUniqueId()));
        }
    }
}
