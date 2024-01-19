package dev.tonimatas.cerium.bridge.world.level.storage;

import net.minecraft.nbt.CompoundTag;

import java.io.File;

public interface PlayerDataStorageBridge {
    CompoundTag getPlayerData(String s);

    File getPlayerDir();
}
