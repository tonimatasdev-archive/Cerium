package dev.tonimatas.cerium.mixins.world.level.storage.loot.predicates;

import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootItemRandomChanceWithLootingCondition.class)
public class LootItemRandomChanceWithLootingConditionMixin {
    @Shadow @Final private float percent;

    @Shadow @Final private float lootingMultiplier;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public boolean test(LootContext lootContext) {
        Entity entity = (Entity)lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);
        int i = 0;
        if (entity instanceof LivingEntity) {
            i = EnchantmentHelper.getMobLooting((LivingEntity)entity);
        }

        // CraftBukkit start - only use lootingModifier if set by Bukkit
        if (lootContext.hasParam(CeriumValues.LOOTING_MOD)) {
            i = lootContext.getParamOrNull(CeriumValues.LOOTING_MOD);
        }
        // CraftBukkit end

        return lootContext.getRandom().nextFloat() < this.percent + (float)i * this.lootingMultiplier;
    }
}
