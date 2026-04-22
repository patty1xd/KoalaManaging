package com.koala.managing.commands;

import com.koala.managing.KoalaManaging;
import com.koala.managing.util.TabUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WarningsCommand implements CommandExecutor, TabCompleter {

    private final KoalaManaging plugin;

    public WarningsCommand(KoalaManaging plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("koala.warnings")) {
            sender.sendMessage(plugin.prefix() + "\u00a7cNo permission."); return true;
        }
        if (args.length < 1) {
            sender.sendMessage(plugin.prefix() + "\u00a7cUsage: /warnings <player>"); return true;
        }
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(plugin.prefix() + "\u00a7cPlayer not found."); return true;
        }
        List<String> warns = plugin.getWarnManager().getWarnings(target.getUniqueId());
        sender.sendMessage(plugin.colorize("&8&m----&r &bWarnings for &f" + target.getName() + " &7(" + warns.size() + ") &8&m----"));
        if (warns.isEmpty()) {
            sender.sendMessage(plugin.colorize("  &7No warnings."));
        } else {
            for (int i = 0; i < warns.size(); i++)
                sender.sendMessage(plugin.colorize("  &7" + (i+1) + ". &f" + warns.get(i)));
        }
        sender.sendMessage(plugin.colorize("&8&m-----------------------------"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return TabUtil.onlinePlayers(args[0]);
        return new ArrayList<>();
    }
}
