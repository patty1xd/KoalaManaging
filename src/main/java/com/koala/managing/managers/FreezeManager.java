package com.koala.managing.managers;

import com.koala.managing.KoalaManaging;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezeManager {

    private final KoalaManaging plugin;
    private final Set<UUID> frozen = new HashSet<>();

    public FreezeManager(KoalaManaging plugin) { this.plugin = plugin; }

    public void freeze(UUID uuid) { frozen.add(uuid); }
    public void unfreeze(UUID uuid) { frozen.remove(uuid); }
    public boolean isFrozen(UUID uuid) { return frozen.contains(uuid); }
}
