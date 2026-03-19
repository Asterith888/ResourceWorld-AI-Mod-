package com.asterith.resourceworld;

import net.fabricmc.api.ModInitializer;

public class ResourceWorldMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ResourceWorldCommands.register();
        ResourceWorldManager.initialize();
    }
}
