package dev.tonimatas.cerium.mixins.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BoatDispenseItemBehavior.class)
public class BoatDispenseItemBehaviorMixin {
    @Shadow @Final private DefaultDispenseItemBehavior defaultDispenseItemBehavior;
    @Shadow @Final private boolean isChestBoat;
    @Shadow @Final private Boat.Type type;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
        Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
        ServerLevel serverLevel = blockSource.level();
        Vec3 vec3 = blockSource.center();
        double d = 0.5625 + (double) EntityType.BOAT.getWidth() / 2.0;
        double e = vec3.x() + (double)direction.getStepX() * d;
        double f = vec3.y() + (double)((float)direction.getStepY() * 1.125F);
        double g = vec3.z() + (double)direction.getStepZ() * d;
        BlockPos blockPos = blockSource.pos().relative(direction);
        double h;
        if (serverLevel.getFluidState(blockPos).is(FluidTags.WATER)) {
            h = 1.0;
        } else {
            if (!serverLevel.getBlockState(blockPos).isAir() || !serverLevel.getFluidState(blockPos.below()).is(FluidTags.WATER)) {
                return this.defaultDispenseItemBehavior.dispense(blockSource, itemStack);
            }

            h = 0.0;
        }

        Boat boat = this.isChestBoat ? new ChestBoat(serverLevel, e, f + h, g) : new Boat(serverLevel, e, f + h, g);
        // CraftBukkit start
        ItemStack itemstack1 = itemStack.split(1);
        org.bukkit.block.Block block = CraftBlock.at(serverLevel, blockSource.pos());
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

        BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(e, f + h, g));
        if (!DispenserBlock.eventFired) {
            serverLevel.getCraftServer().getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            itemStack.grow(1);
            return itemStack;
        }

        if (!event.getItem().equals(craftItem)) {
            itemStack.grow(1);
            // Chain to handler for new item
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
            if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != this) {
                idispensebehavior.dispense(blockSource, eventStack);
                return itemStack;
            }
        }

        Boat object = this.isChestBoat ? new ChestBoat(serverLevel, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ()) : new Boat(serverLevel, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ());
        // CraftBukkit end
        EntityType.createDefaultStackConfig(serverLevel, itemStack, (Player)null).accept(boat);
        ((Boat)boat).setVariant(this.type);
        ((Boat)boat).setYRot(direction.toYRot());
        if (!serverLevel.addFreshEntity(object)) itemStack.grow(1); // CraftBukkit
        // itemStack.shrink(1); // CraftBukkit - handled during event processing
        return itemStack;
    }
}
