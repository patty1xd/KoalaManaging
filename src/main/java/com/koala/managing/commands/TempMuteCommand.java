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

public class TempMuteCommand implements CommandExecutor, TabCompleter {

    private final KoalaManaging plugin;

    public TempMuteCommand(KoalaManaging plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("koala.tempmute")) {
            sender.sendMessage(plugin.prefix() + "\u00a7cNo permission.");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(plugin.prefix() + "\u00a7cUsage: /tempmute <player> <duration> <reason>");
            return true;
        }
        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        long duration = DurationParser.parse(args[1]);
        if (duration <= 0) {
            sender.sendMessage(plugin.prefix() + "\u00a7cInvalid duration.");
            return true;
        }
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(plugin.prefix() + "\u00a7cPlayer not found.");
            return true;
        }
        plugin.getMuteManager().addMute(target.getUniqueId(), target.getName(), duration, reason);
        if (target.isOnline()) {
            Player online = target.getPlayer();
            assert online != null;
            online.sendMessage(plugin.prefix() + "\u00a7cYou have been muted for \u00a7f" +
                    DurationParser.format(duration) + "\u00a7c. Reason: \u00a7f" + reason);
        }
        sender.sendMessage(plugin.prefix() + "\u00a7a" + target.getName() +
                " \u00a77has been muted for \u00a7a" + DurationParser.format(duration) +
                "\u00a77. Reason: \u00a7f" + reason);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return TabUtil.onlinePlayers(args[0]);
        if (args.length == 2) return TabUtil.filter(TabUtil.DURATIONS, args[1]);
        if (args.length == 3) return TabUtil.filter(List.of("Spamming", "Toxicity", "Advertising"), args[2]);
        return new ArrayList<>();
    }
}
