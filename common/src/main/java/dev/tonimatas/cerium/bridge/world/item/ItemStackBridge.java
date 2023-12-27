package dev.tonimatas.cerium.bridge.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public interface ItemStackBridge {
    void convertStack(int version);

    void load(CompoundTag compoundTag);

    CompoundTag getTagClone();

    void setTagClone(@Nullable CompoundTag compoundTag);

    void setItem(Item item);
}
