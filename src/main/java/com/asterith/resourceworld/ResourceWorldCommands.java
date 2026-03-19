package com.asterith.resourceworld;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ResourceWorldCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess) {

        dispatcher.register(
            literal("resourceworld")
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
                .then(literal("delete")
                    .then(argument("id", StringArgumentType.string())
                    .executes(ctx -> {
                        String id = StringArgumentType.getString(ctx, "id");
                        boolean ok = ResourceWorldManager.deleteWorld(id);
                        if (ok) {
                            ctx.getSource().sendFeedback(
                                () -> Text.literal("Deleted resource world folder '" + id + "'. Restart the server to regenerate it."),
                                false
                            );
                        } else {
                            ctx.getSource().sendFeedback(
                                () -> Text.literal("Unknown or missing resource world: " + id),
                                false
                            );
                        }
                        return 1;
                    })))
        );
    }
}
