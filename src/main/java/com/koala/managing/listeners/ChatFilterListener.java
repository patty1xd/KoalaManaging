package com.koala.managing.listeners;

import com.koala.managing.KoalaManaging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatFilterListener implements Listener {

    private final KoalaManaging plugin;

    public ChatFilterListener(KoalaManaging plugin) {
        this.plugin = plugin;
    }

    // Run LOW so we check before FFA's NORMAL priority handler cancels the event
    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        if (!plugin.getConfig().getBoolean("chat-filter.enabled", true)) return;

        Player player = event.getPlayer();
        // Ops bypass the filter
        if (player.isOp() || player.hasPermission("koala.filter.bypass")) return;

        String message = event.getMessage().toLowerCase();
        List<String> bannedWords = plugin.getConfig().getStringList("chat-filter.banned-words");

        for (String word : bannedWords) {
            if (word == null || word.isBlank()) continue;
            if (message.contains(word.toLowerCase())) {
                // Just block the message — no muting.
                event.setCancelled(true);
                player.sendMessage(plugin.colorize(
                        plugin.getConfig().getString("chat-filter.block-message",
                                "&cYour message was blocked by the chat filter.")));
                return; // Stop after first matched word
            }
        }
    }
}
