package dev.tonimatas.cerium.mixins.world.entity.ai.behavior;

import dev.tonimatas.cerium.bridge.world.level.LevelBridge;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BehaviorUtils.class)
public class BehaviorUtilsMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static void throwItem(LivingEntity livingEntity, ItemStack itemStack, Vec3 vec3, Vec3 vec32, float f) {
        if (itemStack.isEmpty()) return; // CraftBukkit - SPIGOT-4940: no empty loot
        double d = livingEntity.getEyeY() - (double)f;
        ItemEntity itemEntity = new ItemEntity(livingEntity.level(), livingEntity.getX(), d, livingEntity.getZ(), itemStack);
        itemEntity.setThrower(livingEntity);
        Vec3 vec33 = vec3.subtract(livingEntity.position());
        vec33 = vec33.normalize().multiply(vec32.x, vec32.y, vec32.z);
        itemEntity.setDeltaMovement(vec33);
        itemEntity.setDefaultPickUpDelay();
        // CraftBukkit start
        org.bukkit.event.entity.EntityDropItemEvent event = new org.bukkit.event.entity.EntityDropItemEvent(livingEntity.getBukkitEntity(), (org.bukkit.entity.Item) itemEntity.getBukkitEntity());
        ((LevelBridge) itemEntity.level()).getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        // CraftBukkit end
        livingEntity.level().addFreshEntity(itemEntity);
    }
}
