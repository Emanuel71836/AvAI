package com.avai.event;

import com.avai.AdvancedAIMod;
import com.avai.pathfinding.PathfindingService;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkLoadListener {
    public static void register(PathfindingService pathfindingService) {
        ServerChunkEvents.CHUNK_LOAD.register((ServerLevel world, LevelChunk chunk) -> {
            AdvancedAIMod.LOGGER.debug("Chunk loaded: {}", chunk.getPos());
        });

        ServerChunkEvents.CHUNK_UNLOAD.register((ServerLevel world, LevelChunk chunk) -> {
            AdvancedAIMod.LOGGER.debug("Chunk unloaded: {}", chunk.getPos());
        });
    }
}