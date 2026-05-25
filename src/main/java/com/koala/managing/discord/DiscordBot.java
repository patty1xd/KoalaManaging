package com.koala.managing.discord;

import com.koala.managing.KoalaManaging;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.awt.Color;
import java.time.Instant;
import java.util.EnumSet;

/**
 * Owns the JDA Discord bot connection.
 *
 * Lifecycle:
 *   • {@link #start()} reads {@code discord.bot-token} + {@code discord.report-channel-id}
 *     from config, builds a lightweight JDA instance, and connects.
 *   • {@link #shutdown()} cleanly closes the gateway connection on plugin disable.
 *
 * Outbound only — we don't read messages or react to commands from Discord, so
 * we use {@code createLight} with no privileged intents. That means setting
 * up the bot in the Discord Developer Portal does NOT require enabling
 * "Message Content" or "Presence" intents; the default scope is enough.
 *
 * The bot needs the {@code Send Messages} + {@code Embed Links} permissions
 * on the target channel. Invite URL template:
 *   https://discord.com/oauth2/authorize?client_id=APP_ID&scope=bot&permissions=18432
 */
public final class DiscordBot {

    private final KoalaManaging plugin;
    private volatile JDA jda;
    private volatile long reportChannelId;
    private volatile boolean ready = false;

    public DiscordBot(KoalaManaging plugin) {
        this.plugin = plugin;
    }

    /** Build + connect the bot. Returns immediately; readiness is async. */
    public void start() {
        String token = plugin.getConfig().getString("discord.bot-token", "");
        String channelStr = plugin.getConfig().getString("discord.report-channel-id", "");

        if (token == null || token.isBlank() || token.contains("YOUR_BOT_TOKEN_HERE")) {
            plugin.getLogger().warning("Discord bot disabled — set discord.bot-token in config.yml.");
            return;
        }
        if (channelStr == null || channelStr.isBlank() || channelStr.contains("YOUR_CHANNEL_ID")) {
            plugin.getLogger().warning("Discord bot disabled — set discord.report-channel-id in config.yml.");
            return;
        }
        try {
            this.reportChannelId = Long.parseLong(channelStr.trim());
        } catch (NumberFormatException ex) {
            plugin.getLogger().warning("discord.report-channel-id is not a valid Discord ID: " + channelStr);
            return;
        }

        try {
            this.jda = JDABuilder
                    .createLight(token, EnumSet.noneOf(GatewayIntent.class))
                    .build();
            // awaitReady() blocks; do it on the JDA thread by chaining a callback.
            this.jda.getGatewayPool().submit(() -> {
                try {
                    jda.awaitReady();
                    ready = true;
                    plugin.getLogger().info("Discord bot connected as " +
                            jda.getSelfUser().getAsTag());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            });
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to start Discord bot: " + e.getMessage());
        }
    }

    public void shutdown() {
        ready = false;
        if (jda != null) {
            try { jda.shutdown(); } catch (Throwable ignored) {}
            jda = null;
        }
    }

    public boolean isReady() {
        return ready && jda != null;
    }

    /**
     * Send a player-report embed to the configured channel.
     *
     * Safe to call from the main thread — JDA's {@code .queue()} dispatches
     * the HTTP request on its own thread pool.
     */
    public void sendReport(String reporter, String reported, String reason) {
        if (!isReady()) {
            plugin.getLogger().warning("Discord bot not ready; dropping report from "
                    + reporter + " on " + reported);
            return;
        }
        TextChannel channel = jda.getTextChannelById(reportChannelId);
        if (channel == null) {
            plugin.getLogger().warning("Discord channel " + reportChannelId
                    + " not found — is the bot in the right guild and does it have access?");
            return;
        }

        String title  = plugin.getConfig().getString("discord.report.title",  "New Player Report");
        String footer = plugin.getConfig().getString("discord.report.footer", "KoalaManaging");
        int    color  = plugin.getConfig().getInt   ("discord.report.color",  0xE74C3C); // red

        // Discord field-value limit is 1024 chars; trim defensively.
        String safeReason = reason == null ? "(no reason given)" : reason;
        if (safeReason.length() > 1000) safeReason = safeReason.substring(0, 1000) + "…";

        MessageEmbed embed = new EmbedBuilder()
                .setTitle(title)
                .setColor(new Color(color))
                .addField("Reporter", reporter, true)
                .addField("Reported", reported, true)
                .addField("Reason",   safeReason, false)
                .setTimestamp(Instant.now())
                .setFooter(footer, null)
                .build();

        channel.sendMessageEmbeds(embed).queue(
                ok -> {},
                err -> plugin.getLogger().warning("Discord report send failed: " + err.getMessage())
        );
    }
}
