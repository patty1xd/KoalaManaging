package com.koala.managing.commands;

import com.koala.managing.KoalaManaging;
import com.koala.managing.managers.BanManager;
import com.koala.managing.util.TabUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UnbanCommand implements CommandExecutor, TabCompleter {

    private final KoalaManaging plugin;

    public UnbanCommand(KoalaManaging plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("koala.unban")) {
            sender.sendMessage(plugin.prefix() + "\u00a7cNo permission."); return true;
        }
        if (args.length < 1) {
            sender.sendMessage(plugin.prefix() + "\u00a7cUsage: /unban <player>"); return true;
        }
        BanManager bm = plugin.getBanManager();
        UUID found = null;
        for (Map.Entry<UUID, String> e : bm.getNames().entrySet()) {
            if (e.getValue().equalsIgnoreCase(args[0])) { found = e.getKey(); break; }
        }
        if (found == null || !bm.isBanned(found)) {
            sender.sendMessage(plugin.prefix() + "\u00a7c" + args[0] + " is not banned."); return true;
        }
        bm.removeBan(found);
        sender.sendMessage(plugin.prefix() + "\u00a7a" + args[0] + " \u00a77has been unbanned.");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return TabUtil.filter(new ArrayList<>(plugin.getBanManager().getNames().values()), args[0]);
        return new ArrayList<>();
    }
}
