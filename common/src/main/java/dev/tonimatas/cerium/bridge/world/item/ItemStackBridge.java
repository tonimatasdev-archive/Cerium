package dev.tonimatas.cerium.bridge.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public interface ItemStackBridge {
    void bridge$convertStack(int version);

    void bridge$load(CompoundTag compoundTag);

    CompoundTag bridge$getTagClone();

    void bridge$setTagClone(@Nullable CompoundTag compoundTag):

    void bridge$setItem(Item item);
}
