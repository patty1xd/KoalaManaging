package com.koala.managing.commands;

import com.koala.managing.KoalaManaging;
import com.koala.managing.util.TabUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FreezeCommand implements CommandExecutor, TabCompleter {

    private final KoalaManaging plugin;

    public FreezeCommand(KoalaManaging plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("koala.freeze")) {
            sender.sendMessage(plugin.prefix() + "\u00a7cNo permission."); return true;
        }
        if (args.length < 1) {
            sender.sendMessage(plugin.prefix() + "\u00a7cUsage: /freeze <player>"); return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.prefix() + "\u00a7cPlayer not online."); return true;
        }
        if (plugin.getFreezeManager().isFrozen(target.getUniqueId())) {
            sender.sendMessage(plugin.prefix() + "\u00a7c" + target.getName() + " is already frozen."); return true;
        }
        plugin.getFreezeManager().freeze(target.getUniqueId());
        target.sendMessage(plugin.prefix() + "\u00a7cYou have been \u00a7lfrozen\u00a7c by " + sender.getName() + "!");
        if (plugin.getConfig().getBoolean("settings.freeze-blindness", true)) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, false, false));
        }
        sender.sendMessage(plugin.prefix() + "\u00a7a" + target.getName() + " \u00a77has been frozen.");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return TabUtil.onlinePlayers(args[0]);
        return new ArrayList<>();
    }
}
