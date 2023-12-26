package dev.tonimatas.cerium.mixins.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoteBlock.class)
public class NoteBlockMixin {
    @Inject(method = "playNote", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;blockEvent(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;II)V", shift = At.Shift.BEFORE), cancellable = true)
    private void cerium$playNote(Entity entity, BlockState blockState, Level level, BlockPos blockPos, CallbackInfo ci) {
        org.bukkit.event.block.NotePlayEvent event = org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory.callNotePlayEvent(level, blockPos, blockState.getValue(NoteBlock.INSTRUMENT), blockState.getValue(NoteBlock.NOTE));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
