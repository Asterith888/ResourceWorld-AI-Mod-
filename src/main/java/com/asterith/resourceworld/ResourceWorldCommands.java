package com.asterith.resourceworld;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class ResourceWorldCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess) {

        dispatcher.register(
            literal("resourceworld")
                .then(literal("create")
                    .then(argument("id", StringArgumentType.string())
                    .then(argument("type", StringArgumentType.string())
                    .executes(ctx -> {
                        String id = StringArgumentType.getString(ctx, "id");
                        String type = StringArgumentType.getString(ctx, "type");
                        ResourceWorldManager.createWorld(id, type);
                        return 1;
                    }))))
                .then(literal("delete")
                    .then(argument("id", StringArgumentType.string())
                    .executes(ctx -> {
                        String id = StringArgumentType.getString(ctx, "id");
                        ResourceWorldManager.deleteWorld(id);
                        return 1;
                    })))
                .then(literal("tp")
                    .then(argument("id", StringArgumentType.string())
                    .executes(ctx -> {
                        String id = StringArgumentType.getString(ctx, "id");
                        ResourceWorldManager.teleport(ctx.getSource().getPlayer(), id);
                        return 1;
                    })))
                .then(literal("home")
                    .executes(ctx -> {
                        ResourceWorldManager.teleportHome(ctx.getSource().getPlayer());
                        return 1;
                    }))
        );
    }
}
