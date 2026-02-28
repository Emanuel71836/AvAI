package com.avai.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalEventSystem {
    private static final Map<Level, HitEvent> RECENT_HITS = new ConcurrentHashMap<>();
    private static final long HIT_TIMEOUT_MS = 5000; // 5 seconds

    public static void recordHit(LivingEntity victim, Player attacker) {
        if (!victim.level().isClientSide()) {
            RECENT_HITS.put(victim.level(), new HitEvent(victim, attacker));
        }
    }

    public static HitEvent getRecentHit(Level level) {
        HitEvent event = RECENT_HITS.get(level);
        if (event != null && System.currentTimeMillis() - event.timestamp < HIT_TIMEOUT_MS) {
            return event;
        }
        RECENT_HITS.remove(level);
        return null;
    }
}