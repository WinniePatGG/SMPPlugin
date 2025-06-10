package de.winniepat.SMPPlugin.blockelevator;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {
    private static final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME_MS = 2;

    public static boolean isOnCooldown(UUID player) {
        if (!cooldowns.containsKey(player)) return false;
        long lastUsed = cooldowns.get(player);
        return (System.currentTimeMillis() - lastUsed) < COOLDOWN_TIME_MS;
    }

    public static void setCooldown(UUID player) {
        cooldowns.put(player, System.currentTimeMillis());
    }
}
