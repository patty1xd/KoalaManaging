package com.koala.managing.commands;

import com.koala.managing.KoalaManaging;
import com.koala.managing.util.TabUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WarnCommand implements CommandExecutor, TabCompleter {

    private final KoalaManaging plugin;

    public WarnCommand(KoalaManaging plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("koala.warn")) {
            sender.sendMessage(plugin.prefix() + "\u00a7cNo permission."); return true;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.prefix() + "\u00a7cUsage: /warn <player> <reason>"); return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.prefix() + "\u00a7cPlayer not online."); return true;
        }
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        plugin.getWarnManager().addWarning(target.getUniqueId(), reason);
        int count = plugin.getWarnManager().getWarningCount(target.getUniqueId());
        target.sendMessage(plugin.prefix() + "\u00a7cYou have been warned by \u00a7f" + sender.getName() +
                "\u00a7c. Reason: \u00a7f" + reason + "\u00a7c. Total warnings: \u00a7f" + count);
        sender.sendMessage(plugin.prefix() + "\u00a7aWarned \u00a7f" + target.getName() +
                "\u00a7a (\u00a7f" + count + " \u00a7awarning" + (count != 1 ? "s" : "") + ").");
        int max = plugin.getConfig().getInt("settings.max-warnings-before-ban", 3);
        if (count >= max) {
            Bukkit.broadcastMessage(plugin.prefix() + "\u00a7c" + target.getName() + " has reached " + count + " warnings!");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return TabUtil.onlinePlayers(args[0]);
        if (args.length == 2) return TabUtil.filter(List.of("Hacking", "Spam", "Toxicity", "Griefing"), args[1]);
        return new ArrayList<>();
    }
}
