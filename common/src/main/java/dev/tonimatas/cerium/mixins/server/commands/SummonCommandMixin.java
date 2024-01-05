package dev.tonimatas.cerium.mixins.server.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SummonCommand.class)
public class SummonCommandMixin {
    @Inject(method = "createEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tryAddFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)Z", shift = At.Shift.BEFORE))
    private static void cerium$createEntity(CommandSourceStack commandSourceStack, Holder.Reference<EntityType<?>> reference, Vec3 vec3, CompoundTag compoundTag, boolean bl, CallbackInfoReturnable<Entity> cir) {
        ((ServerLevel) commandSourceStack.getLevel()).tryAddFreshEntityWithPassengersCause(CreatureSpawnEvent.SpawnReason.COMMAND); // CraftBukkit - pass a spawn reason of "COMMAND"
    }
}
