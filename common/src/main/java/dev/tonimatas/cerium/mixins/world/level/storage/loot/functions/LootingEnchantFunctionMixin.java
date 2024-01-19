package dev.tonimatas.cerium.mixins.world.level.storage.loot.functions;

import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LootingEnchantFunction.class)
public abstract class LootingEnchantFunctionMixin {
    @Shadow @Final private NumberProvider value;
    @Shadow @Final private int limit;
    @Shadow protected abstract boolean hasLimit();

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        Entity entity = (Entity)lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (entity instanceof LivingEntity) {
            int i = EnchantmentHelper.getMobLooting((LivingEntity)entity);
            // CraftBukkit start - use lootingModifier if set by plugin
            if (lootContext.hasParam(CeriumValues.LOOTING_MOD)) {
                i = lootContext.getParamOrNull(CeriumValues.LOOTING_MOD);
            }
            // CraftBukkit end
            if (i <= 0) { // CraftBukkit - account for possible negative looting values from Bukkit
                return itemStack;
            }

            float f = (float)i * this.value.getFloat(lootContext);
            itemStack.grow(Math.round(f));
            if (this.hasLimit() && itemStack.getCount() > this.limit) {
                itemStack.setCount(this.limit);
            }
        }

        return itemStack;
    }
}
