package com.koala.managing.commands;

import com.koala.managing.KoalaManaging;
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

/**
 * /report &lt;player&gt; &lt;reason&gt;
 *
 *   • Any player can use it (no permission needed by default).
 *   • Per-reporter cooldown configurable in config.yml (default 60s).
 *   • Cannot report yourself; player must be online or have-been online.
 *   • Sends an embed to the configured Discord webhook with reporter,
 *     reported, reason, and timestamp.
 *   • Online staff with {@code koala.report.notify} get an in-game alert.
 */
public class ReportCommand implements CommandExecutor, TabCompleter {

    private final KoalaManaging plugin;

    public ReportCommand(KoalaManaging plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player reporter)) {
            sender.sendMessage(plugin.prefix() + "§cOnly players can file reports.");
            return true;
        }

        if (args.length < 2) {
            reporter.sendMessage(plugin.prefix() + "§cUsage: /report <player> <reason>");
            return true;
        }

        String targetName = args[0];
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();

        int minLen = plugin.getConfig().getInt("report.min-reason-length", 4);
        if (reason.length() < minLen) {
            reporter.sendMessage(plugin.prefix() + "§cReason must be at least " + minLen + " characters.");
            return true;
        }

        if (targetName.equalsIgnoreCase(reporter.getName())) {
            reporter.sendMessage(plugin.prefix() + "§cYou can't report yourself.");
            return true;
        }

        Player onlineTarget = Bukkit.getPlayerExact(targetName);
        String resolvedName;
        if (onlineTarget != null) {
            resolvedName = onlineTarget.getName();
        } else {
            // Allow reporting recently-logged-off players (anticheat / combat-log).
            OfflinePlayer off = Bukkit.getOfflinePlayer(targetName);
            if (off == null || off.getName() == null || !off.hasPlayedBefore()) {
                reporter.sendMessage(plugin.prefix() + "§cThat player has never been on this server.");
                return true;
            }
            resolvedName = off.getName();
        }

        long wait = plugin.getReportManager().secondsUntilReady(reporter.getUniqueId());
        if (wait > 0) {
            reporter.sendMessage(plugin.prefix() + "§cPlease wait §e" + wait + "s§c before reporting again.");
            return true;
        }

        // Fire to Discord via the bot (JDA handles its own async I/O)
        plugin.getDiscordBot().sendReport(reporter.getName(), resolvedName, reason);
        plugin.getReportManager().markReported(reporter.getUniqueId());

        // Confirm to reporter
        reporter.sendMessage(plugin.prefix() + "§aYour report on §f" + resolvedName +
                "§a has been submitted. Staff will review it shortly.");

        // In-game alert for online staff
        if (plugin.getConfig().getBoolean("report.notify-staff", true)) {
            String alert = plugin.prefix() + "§e⚠ §f" + reporter.getName() +
                    " §7reported §f" + resolvedName +
                    " §7for: §f" + truncate(reason, 80);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("koala.report.notify")) p.sendMessage(alert);
            }
            Bukkit.getConsoleSender().sendMessage(alert);
        }

        return true;
    }

    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return TabUtil.onlinePlayers(args[0]);
        if (args.length == 2) return TabUtil.filter(
                List.of("Hacking", "Cheating", "Reach", "Killaura", "Spam", "Toxicity", "Griefing"),
                args[1]);
        return new ArrayList<>();
    }
}
