package com.asterith.resourceworld;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSavePath;

import java.nio.file.Path;

public class WorldSavePathAccessor {

    public static Path getDimensionsPath(MinecraftServer server) {
        // This resolves: <world>/dimensions/
        return server.getSavePath(WorldSavePath.ROOT).resolve("dimensions");
    }
}
