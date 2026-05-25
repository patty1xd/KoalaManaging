package com.koala.managing.managers;

import com.koala.managing.KoalaManaging;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks per-reporter cooldowns so a single user can't spam reports.
 * Pure in-memory — reports themselves live in Discord, not on disk.
 */
public class ReportManager {

    private final KoalaManaging plugin;
    private final Map<UUID, Long> lastReportAt = new HashMap<>();

    public ReportManager(KoalaManaging plugin) {
        this.plugin = plugin;
    }

    /** @return seconds remaining on this reporter's cooldown, or 0 if ready. */
    public long secondsUntilReady(UUID reporter) {
        long cooldownSec = plugin.getConfig().getLong("report.cooldown-seconds", 60);
        Long last = lastReportAt.get(reporter);
        if (last == null) return 0;
        long elapsed = (System.currentTimeMillis() - last) / 1000L;
        long remaining = cooldownSec - elapsed;
        return Math.max(0, remaining);
    }

    public void markReported(UUID reporter) {
        lastReportAt.put(reporter, System.currentTimeMillis());
    }
}
