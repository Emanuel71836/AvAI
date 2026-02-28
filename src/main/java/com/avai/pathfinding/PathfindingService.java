package com.avai.pathfinding;

import com.avai.AdvancedAIMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Path;

import java.util.concurrent.CompletableFuture;

public class PathfindingService {
    public CompletableFuture<Path> requestPath(Mob mob, BlockPos goal) {
        CompletableFuture<Path> future = new CompletableFuture<>();
        AdvancedAIMod.MAIN_THREAD_TASKS.add(() -> {
            try {
                Path path = mob.getNavigation().createPath(goal, 1);
                future.complete(path);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}