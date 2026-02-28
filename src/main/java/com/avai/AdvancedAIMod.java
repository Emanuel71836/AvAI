package com.avai;

import com.avai.event.ChunkLoadListener;
import com.avai.event.GlobalEventSystem;
import com.avai.pathfinding.PathfindingService;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.*;

public class AdvancedAIMod implements ModInitializer {
    public static final String MOD_ID = "avai";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final int CORE_THREADS = 4;
    public static ExecutorService AI_EXECUTOR;

    public static final Queue<Runnable> MAIN_THREAD_TASKS = new ConcurrentLinkedQueue<>();
    public static PathfindingService PATHFINDING;

    @Override
    public void onInitialize() {
        LOGGER.info("Advanced AI Mod initializing.");

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.info("Server fully started – now initializing AI systems.");
            AI_EXECUTOR = Executors.newFixedThreadPool(CORE_THREADS);
            PATHFINDING = new PathfindingService();
            ChunkLoadListener.register(PATHFINDING);
            LOGGER.info("AI systems ready with {} worker threads.", CORE_THREADS);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> shutdownExecutor());

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int processed = 0, MAX_TASKS_PER_TICK = 50;
            Runnable task;
            while (processed < MAX_TASKS_PER_TICK && (task = MAIN_THREAD_TASKS.poll()) != null) {
                try {
                    task.run();
                } catch (Exception e) {
                    LOGGER.error("Error processing main thread task", e);
                }
                processed++;
            }
            if (processed == MAX_TASKS_PER_TICK && !MAIN_THREAD_TASKS.isEmpty()) {
                LOGGER.debug("Main task queue backlog: {} tasks", MAIN_THREAD_TASKS.size());
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide() && entity instanceof LivingEntity living) {
                GlobalEventSystem.recordHit(living, player);
            }
            return InteractionResult.PASS;
        });
    }

    private static void shutdownExecutor() {
        if (AI_EXECUTOR != null && !AI_EXECUTOR.isShutdown()) {
            AI_EXECUTOR.shutdown();
            try {
                if (!AI_EXECUTOR.awaitTermination(3, TimeUnit.SECONDS)) {
                    AI_EXECUTOR.shutdownNow();
                }
            } catch (InterruptedException e) {
                AI_EXECUTOR.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
