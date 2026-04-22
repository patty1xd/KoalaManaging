package com.koala.managing.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabUtil {

    public static List<String> onlinePlayers(String partial) {
        List<String> result = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().startsWith(partial.toLowerCase()))
                result.add(p.getName());
        }
        return result;
    }

    public static List<String> filter(List<String> options, String partial) {
        List<String> result = new ArrayList<>();
        for (String opt : options)
            if (opt.toLowerCase().startsWith(partial.toLowerCase())) result.add(opt);
        return result;
    }

    public static final List<String> DURATIONS = List.of(
        "30s","1m","5m","10m","30m","1h","6h","12h","1d","3d","7d","14d","30d"
    );
}
