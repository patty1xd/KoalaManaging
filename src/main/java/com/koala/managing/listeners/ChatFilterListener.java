package com.koala.managing.listeners;

import com.koala.managing.KoalaManaging;
import com.koala.managing.managers.MuteManager;
import com.koala.managing.util.DurationParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class ChatFilterListener implements Listener {

    private final KoalaManaging plugin;

    public ChatFilterListener(KoalaManaging plugin) {
        this.plugin = plugin;
    }

    // Run HIGHEST so we check after ChatMuteListener already cancelled muted players
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        if (!plugin.getConfig().getBoolean("chat-filter.enabled", true)) return;

        Player player = event.getPlayer();
        // Ops bypass the filter
        if (player.isOp() || player.hasPermission("koala.filter.bypass")) return;

        String message = event.getMessage().toLowerCase();
        Set<String> words = plugin.getConfig().getConfigurationSection("chat-filter.words").getKeys(false);

        for (String word : words) {
            if (containsWord(message, word)) {
                event.setCancelled(true);

                String muteDuration = plugin.getConfig().getString("chat-filter.words." + word + ".mute", "none");

                if (!muteDuration.equalsIgnoreCase("none")) {
                    long duration = DurationParser.parse(muteDuration);
                    if (duration > 0) {
                        MuteManager mm = plugin.getMuteManager();
                        // Only mute if not already muted
                        if (!mm.isMuted(player.getUniqueId())) {
                            mm.addMute(player.getUniqueId(), player.getName(), duration, "Automatic: blocked word");
                            String muteMsg = plugin.getConfig().getString("chat-filter.mute-message",
                                    "&cYou have been automatically muted for &f{duration}&c.")
                                    .replace("{duration}", DurationParser.format(duration));
                            player.sendMessage(plugin.colorize(muteMsg));

                            // Notify staff async-safely
                            if (plugin.getConfig().getBoolean("chat-filter.notify-staff", true)) {
                                String staffMsg = plugin.getConfig().getString(
                                        "chat-filter.staff-notify-message",
                                        "&8[&cFilter&8] &7{player} triggered '&f{word}&7' muted for &f{duration}&7.")
                                        .replace("{player}", player.getName())
                                        .replace("{word}", word)
                                        .replace("{duration}", DurationParser.format(duration));
                                Bukkit.getScheduler().runTask(plugin, () -> {
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (p.hasPermission("koala.tempmute")) {
                                            p.sendMessage(plugin.colorize(staffMsg));
                                        }
                                    }
                                });
                            }
                        } else {
                            player.sendMessage(plugin.colorize(
                                    plugin.getConfig().getString("chat-filter.block-message",
                                            "&cYour message was blocked by the chat filter.")));
                        }
                    }
                } else {
                    player.sendMessage(plugin.colorize(
                            plugin.getConfig().getString("chat-filter.block-message",
                                    "&cYour message was blocked by the chat filter.")));
                }
                return; // Stop after first matched word
            }
        }
    }

    // Checks if the message contains the word as a whole word or substring
    private boolean containsWord(String message, String word) {
        return message.contains(word);
    }
}
