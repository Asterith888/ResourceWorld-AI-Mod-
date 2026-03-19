package com.asterith.resourceworld;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ResourceWorldMod implements ModInitializer {

    @Override
    public void onInitialize() {

        // Load your world metadata, config, etc.
        ResourceWorldManager.initialize();

        // Register commands the Fabric way
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) ->
                ResourceWorldCommands.register(dispatcher, registryAccess)
        );
    }
}
