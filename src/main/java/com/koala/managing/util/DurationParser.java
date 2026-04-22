package com.koala.managing.util;

public class DurationParser {

    public static long parse(String input) {
        if (input == null || input.isEmpty()) return -1;
        input = input.toLowerCase().trim();
        try {
            char unit = input.charAt(input.length() - 1);
            long value = Long.parseLong(input.substring(0, input.length() - 1));
            return switch (unit) {
                case 's' -> value * 1000L;
                case 'm' -> value * 60_000L;
                case 'h' -> value * 3_600_000L;
                case 'd' -> value * 86_400_000L;
                case 'w' -> value * 604_800_000L;
                default -> -1;
            };
        } catch (NumberFormatException e) { return -1; }
    }

    public static String format(long millis) {
        if (millis <= 0) return "0s";
        long seconds = millis / 1000;
        long weeks   = seconds / 604800; seconds %= 604800;
        long days    = seconds / 86400;  seconds %= 86400;
        long hours   = seconds / 3600;   seconds %= 3600;
        long minutes = seconds / 60;     seconds %= 60;
        StringBuilder sb = new StringBuilder();
        if (weeks   > 0) sb.append(weeks).append("w ");
        if (days    > 0) sb.append(days).append("d ");
        if (hours   > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0) sb.append(seconds).append("s");
        return sb.toString().trim();
    }
}
