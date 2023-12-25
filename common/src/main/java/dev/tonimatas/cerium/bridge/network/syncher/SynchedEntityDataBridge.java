package dev.tonimatas.cerium.bridge.network.syncher;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;

public interface SynchedEntityDataBridge {
    <T> void bridge$markDirty(EntityDataAccessor<T> datawatcherobject);

    void bridge$refresh(ServerPlayer to);
}
