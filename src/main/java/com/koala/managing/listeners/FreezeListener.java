package com.koala.managing.listeners;

import com.koala.managing.managers.FreezeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FreezeListener implements Listener {

    private final FreezeManager freezeManager;

    public FreezeListener(FreezeManager freezeManager) { this.freezeManager = freezeManager; }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!freezeManager.isFrozen(player.getUniqueId())) return;
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            event.setTo(event.getFrom());
            player.sendMessage("\u00a7cYou are frozen and cannot move!");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (freezeManager.isFrozen(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("\u00a7cYou are frozen and cannot chat!");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {}
}
