package dev.tonimatas.cerium.mixins.world.entity.vehicle;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftMinecartCommand;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecartCommandBlock.MinecartCommandBase.class)
public class MinecartCommandBaseMixin {
    // CraftBukkit start
    public org.bukkit.command.CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return (CraftMinecartCommand) MinecartCommandBlock.this.getBukkitEntity();
    }
    // CraftBukkit end
}
