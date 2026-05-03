package com.koala.managing.broadcast;

import com.koala.managing.KoalaManaging;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoBroadcastTask extends BukkitRunnable {

    private final KoalaManaging plugin;
    private int index = 0;

    // Matches [display text](url)
    private static final Pattern LINK_PATTERN = Pattern.compile("\\[([^\\]]+)\\]\\(([^)]+)\\)");

    public AutoBroadcastTask(KoalaManaging plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!plugin.getConfig().getBoolean("auto-broadcast.enabled", true)) return;

        List<String> messages = plugin.getConfig().getStringList("auto-broadcast.messages");
        if (messages.isEmpty()) return;

        String raw = messages.get(index % messages.size());
        index++;

        sendClickableMessage(raw);
    }

    private void sendClickableMessage(String raw) {
        Matcher matcher = LINK_PATTERN.matcher(raw);

        if (!matcher.find()) {
            String plain = plugin.colorize(raw);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(plain);
            }
            return;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            TextComponent full = new TextComponent();
            int last = 0;
            matcher.reset();

            while (matcher.find()) {
                if (matcher.start() > last) {
                    String before = plugin.colorize(raw.substring(last, matcher.start()));
                    full.addExtra(new TextComponent(TextComponent.fromLegacyText(before)));
                }

                String displayText = plugin.colorize(matcher.group(1));
                String url = matcher.group(2);

                TextComponent link = new TextComponent(TextComponent.fromLegacyText(displayText));
                link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
                link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(plugin.colorize("&7Click to open: &b" + url)).create()));
                full.addExtra(link);

                last = matcher.end();
            }

            if (last < raw.length()) {
                String after = plugin.colorize(raw.substring(last));
                full.addExtra(new TextComponent(TextComponent.fromLegacyText(after)));
            }

            p.spigot().sendMessage(full);
        }
    }
}
