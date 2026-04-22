package com.koala.managing.commands;

import com.koala.managing.KoalaManaging;
import com.koala.managing.util.DurationParser;
import com.koala.managing.util.TabUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TempBanCommand implements CommandExecutor, TabCompleter {

    private final KoalaManaging plugin;

    public TempBanCommand(KoalaManaging plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("koala.tempban")) {
            sender.sendMessage(plugin.prefix() + plugin.colorize(
                    plugin.getConfig().getString("messages.no-permission", "&cNo permission.")));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(plugin.prefix() + "\u00a7cUsage: /tempban <player> <duration> <reason>");
            sender.sendMessage(plugin.prefix() + "\u00a77Duration examples: 10s, 5m, 2h, 1d, 1w");
            return true;
        }
        String playerName = args[0];
        String durationStr = args[1];
        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        long duration = DurationParser.parse(durationStr);
        if (duration <= 0) {
            sender.sendMessage(plugin.prefix() + "\u00a7cInvalid duration. Use: 10s, 5m, 2h, 1d, 1w");
            return true;
        }
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(plugin.prefix() + plugin.colorize(
                    plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found.")));
            return true;
        }
        if (target.isOnline()) {
            Player online = target.getPlayer();
            assert online != null;
            online.kickPlayer(plugin.colorize("&cYou have been temporarily banned.\n&7Reason: &f" +
                    reason + "\n&7Duration: &f" + DurationParser.format(duration)));
        }
        plugin.getBanManager().addBan(target.getUniqueId(), target.getName(), duration, reason);
        sender.sendMessage(plugin.prefix() + "\u00a7a" + target.getName() +
                " \u00a77has been banned for \u00a7a" + DurationParser.format(duration) +
                "\u00a77. Reason: \u00a7f" + reason);
        Bukkit.broadcastMessage(plugin.prefix() + "\u00a7c" + target.getName() +
                " \u00a77was temporarily banned for \u00a7c" + DurationParser.format(duration) +
                "\u00a77. Reason: \u00a7f" + reason);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return TabUtil.onlinePlayers(args[0]);
        if (args.length == 2) return TabUtil.filter(TabUtil.DURATIONS, args[1]);
        if (args.length == 3) return TabUtil.filter(List.of("Hacking", "Cheating", "Griefing", "Spamming"), args[2]);
        return new ArrayList<>();
    }
}
