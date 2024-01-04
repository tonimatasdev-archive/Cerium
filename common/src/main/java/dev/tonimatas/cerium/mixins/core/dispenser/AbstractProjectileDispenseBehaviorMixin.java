package dev.tonimatas.cerium.mixins.core.dispenser;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractProjectileDispenseBehavior.class)
public abstract class AbstractProjectileDispenseBehaviorMixin {
    @Shadow protected abstract Projectile getProjectile(Level arg, Position arg2, ItemStack arg3);
    @Shadow protected abstract float getPower();
    @Shadow protected abstract float getUncertainty();

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
        Level level = blockSource.level();
        Position position = DispenserBlock.getDispensePosition(blockSource);
        Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
        Projectile projectile = this.getProjectile(level, position, itemStack);
        // CraftBukkit start
        ItemStack itemstack1 = itemStack.split(1);
        org.bukkit.block.Block block = CraftBlock.at(level, blockSource.pos());
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

        BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector((double) direction.getStepX(), (double) ((float) direction.getStepY() + 0.1F), (double) direction.getStepZ()));
        if (!DispenserBlock.eventFired) {
            level.getCraftServer().getPluginManager().callEvent(event);
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

        projectile.shoot(event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), this.getPower(), this.getUncertainty());
        ((Entity) projectile).projectileSource = new org.bukkit.craftbukkit.v1_20_R3.projectiles.CraftBlockProjectileSource(blockSource.blockEntity());
        // CraftBukkit end
        level.addFreshEntity(projectile);
        // itemstack.shrink(1); // CraftBukkit - Handled during event processing
        return itemStack;
    }
}
