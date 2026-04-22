package com.koala.managing.commands;

import com.koala.managing.KoalaManaging;
import com.koala.managing.managers.MuteManager;
import com.koala.managing.util.TabUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UnmuteCommand implements CommandExecutor, TabCompleter {

    private final KoalaManaging plugin;

    public UnmuteCommand(KoalaManaging plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("koala.unmute")) {
            sender.sendMessage(plugin.prefix() + "\u00a7cNo permission."); return true;
        }
        if (args.length < 1) {
            sender.sendMessage(plugin.prefix() + "\u00a7cUsage: /unmute <player>"); return true;
        }
        MuteManager mm = plugin.getMuteManager();
        UUID found = null;
        for (Map.Entry<UUID, String> e : mm.getNames().entrySet()) {
            if (e.getValue().equalsIgnoreCase(args[0])) { found = e.getKey(); break; }
        }
        if (found == null || !mm.isMuted(found)) {
            sender.sendMessage(plugin.prefix() + "\u00a7c" + args[0] + " is not muted."); return true;
        }
        mm.removeMute(found);
        sender.sendMessage(plugin.prefix() + "\u00a7a" + args[0] + " \u00a77has been unmuted.");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return TabUtil.filter(new ArrayList<>(plugin.getMuteManager().getNames().values()), args[0]);
        return new ArrayList<>();
    }
}
