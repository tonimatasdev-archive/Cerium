package dev.tonimatas.cerium.mixins.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MapItem.class)
public abstract class MapItemMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @SuppressWarnings("OverwriteModifiers")
    @Overwrite
    public static Integer getMapId(ItemStack stack) {
        CompoundTag compoundnbt = stack.getTag();
        return compoundnbt != null && compoundnbt.contains("map", 99) ? compoundnbt.getInt("map") : -1;
    }
}
