package com.asterith.resourceworld;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResourceWorldManager {

    private static final String MOD_ID = "resourceworld";
    private static final String OVERWORLD_ID = "overworld";
    private static final String NETHER_ID = "nether";

    private static MinecraftServer server;

    // Simple in-memory home storage (per session)
    private static final Map<UUID, HomeLocation> HOMES = new HashMap<>();

    public static void initialize() {
        // Capture server instance when it starts
        ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);
    }

    // Called from /resourceworld create <id> <type>
    public static void createWorld(String id, String type) {
        if (server == null) return;

        id = id.toLowerCase();
        if (!id.equals(OVERWORLD_ID) && !id.equals(NETHER_ID)) {
            // Only allow the two fixed IDs
            return;
        }

        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(MOD_ID, id));
        ServerWorld existing = server.getWorld(worldKey);
        if (existing != null) {
            // Already exists, nothing to do
            return;
        }

        // Write dimension JSON (stub – you can expand this to real JSON later)
        writeDimensionJson(id, type);

        // At this point, in a real setup, the dimension would be created by data/pack loading.
        // We assume the world becomes available after reload / next startup.
        ServerWorld world = server.getWorld(worldKey);
        if (world != null) {
            applyWorldBorder(world);
        }
    }

    // Called from /resourceworld delete <id>
    public static void deleteWorld(String id) {
        if (server == null) return;

        id = id.toLowerCase();
        if (!id.equals(OVERWORLD_ID) && !id.equals(NETHER_ID)) {
            return;
        }

        // Delete dimension folder under the save
        Path dimPath = server.getSavePath(WorldSavePathAccessor.getDimensionsPath())
                .resolve(MOD_ID)
                .resolve(id);

        try {
            if (Files.exists(dimPath)) {
                deleteRecursively(dimPath);
            }
        } catch (IOException e) {
            // You can add logging here if you want
        }

        // Optionally delete JSON definition if you store it somewhere specific
        // (e.g., config/resourceworld/<id>.json)
    }

    // Called from /resourceworld tp <id>
    public static void teleport(ServerPlayerEntity player, String id) {
        if (server == null || player == null) return;

        id = id.toLowerCase();
        if (!id.equals(OVERWORLD_ID) && !id.equals(NETHER_ID)) {
            player.sendMessage(Text.literal("Unknown resource world: " + id), false);
            return;
        }

        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(MOD_ID, id));
        ServerWorld target = server.getWorld(worldKey);
        if (target == null) {
            player.sendMessage(Text.literal("Resource world '" + id + "' does not exist yet. Use /resourceworld create " + id + " mirror"), false);
            return;
        }

        // Save home before teleport
        saveHome(player);

        BlockPos spawn = target.getSpawnPos();
        double x = spawn.getX() + 0.5;
        double y = spawn.getY();
        double z = spawn.getZ() + 0.5;

        player.teleport(target, x, y, z, player.getYaw(), player.getPitch());
        player.sendMessage(Text.literal("Teleported to resource world: " + id), false);
    }

    // Called from /resourceworld home
    public static void teleportHome(ServerPlayerEntity player) {
        if (server == null || player == null) return;

        HomeLocation home = HOMES.get(player.getUuid());
        if (home == null) {
            player.sendMessage(Text.literal("No home location saved."), false);
            return;
        }

        ServerWorld world = server.getWorld(home.dimension());
        if (world == null) {
            player.sendMessage(Text.literal("Home world is not available."), false);
            return;
        }

        player.teleport(world,
                home.pos().getX() + 0.5,
                home.pos().getY(),
                home.pos().getZ() + 0.5,
                home.yaw(),
                home.pitch());
        player.sendMessage(Text.literal("Returned home."), false);
    }

    private static void saveHome(ServerPlayerEntity player) {
        RegistryKey<World> dim = player.getWorld().getRegistryKey();
        BlockPos pos = player.getBlockPos();
        float yaw = player.getYaw();
        float pitch = player.getPitch();
        HOMES.put(player.getUuid(), new HomeLocation(dim, pos, yaw, pitch));
    }

    private static void applyWorldBorder(ServerWorld world) {
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0.0, 0.0);
        border.setSize(20000.0); // 10,000 block radius
    }

    private static void writeDimensionJson(String id, String type) {
        // This is a stub. You can later implement writing real JSON files
        // under src/main/resources/data/resourceworld/dimension/<id>.json
        // or a config folder, depending on how you want to handle dynamic worlds.
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (var stream = Files.list(path)) {
                for (Path child : stream.toList()) {
                    deleteRecursively(child);
                }
            }
        }
        Files.deleteIfExists(path);
    }

    private record HomeLocation(RegistryKey<World> dimension, BlockPos pos, float yaw, float pitch) {}
}
