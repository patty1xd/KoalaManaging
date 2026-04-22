package com.koala.managing.commands;

import com.koala.managing.KoalaManaging;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BroadcastCommand implements CommandExecutor {

    private final KoalaManaging plugin;

    public BroadcastCommand(KoalaManaging plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("koala.broadcast")) {
            sender.sendMessage(plugin.prefix() + "\u00a7cNo permission."); return true;
        }
        if (args.length < 1) {
            sender.sendMessage(plugin.prefix() + "\u00a7cUsage: /broadcast <message>"); return true;
        }
        String rawMessage = String.join(" ", args);
        String format = plugin.getConfig().getString("messages.broadcast-format", "&4&l[BROADCAST] &r&f{message}");
        String formatted = plugin.colorize(format.replace("{message}", rawMessage));

        Bukkit.broadcastMessage(formatted);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(plugin.colorize("&4&l[BROADCAST]"), plugin.colorize("&f" + rawMessage), 10, 80, 20);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(plugin.colorize("&4&l[BROADCAST] &f" + rawMessage)));
        }
        return true;
    }
}
