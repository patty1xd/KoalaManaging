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

public class KickCommand implements CommandExecutor, TabCompleter {

    private final KoalaManaging plugin;

    public KickCommand(KoalaManaging plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("koala.kick")) {
            sender.sendMessage(plugin.prefix() + "\u00a7cNo permission."); return true;
        }
        if (args.length < 1) {
            sender.sendMessage(plugin.prefix() + "\u00a7cUsage: /kick <player> [reason]"); return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.prefix() + "\u00a7cPlayer not online."); return true;
        }
        String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "You have been kicked.";
        target.kickPlayer(plugin.colorize("&cYou have been kicked.\n&7Reason: &f" + reason));
        Bukkit.broadcastMessage(plugin.prefix() + "\u00a7c" + target.getName() + " \u00a77was kicked. Reason: \u00a7f" + reason);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return TabUtil.onlinePlayers(args[0]);
        if (args.length == 2) return TabUtil.filter(List.of("Breaking rules", "Hacking", "Spam"), args[1]);
        return new ArrayList<>();
    }
}
