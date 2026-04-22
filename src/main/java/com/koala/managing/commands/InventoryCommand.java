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
import java.util.List;

public class InventoryCommand implements CommandExecutor, TabCompleter {

    private final KoalaManaging plugin;

    public InventoryCommand(KoalaManaging plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("koala.inventory")) {
            sender.sendMessage(plugin.prefix() + "\u00a7cNo permission."); return true;
        }
        if (args.length < 2 || !args[1].equalsIgnoreCase("clear")) {
            sender.sendMessage(plugin.prefix() + "\u00a7cUsage: /inventory <player> clear"); return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.prefix() + "\u00a7cPlayer not online."); return true;
        }
        target.getInventory().clear();
        sender.sendMessage(plugin.prefix() + "\u00a7aCleared inventory of \u00a7f" + target.getName() + "\u00a7a.");
        target.sendMessage(plugin.prefix() + "\u00a7cYour inventory was cleared by \u00a7f" + sender.getName() + "\u00a7c.");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return TabUtil.onlinePlayers(args[0]);
        if (args.length == 2) return TabUtil.filter(List.of("clear"), args[1]);
        return new ArrayList<>();
    }
}
