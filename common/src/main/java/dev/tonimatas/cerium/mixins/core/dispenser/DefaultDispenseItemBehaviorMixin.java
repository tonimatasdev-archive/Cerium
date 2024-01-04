package dev.tonimatas.cerium.mixins.core.dispenser;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftVector;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DefaultDispenseItemBehavior.class)
public class DefaultDispenseItemBehaviorMixin {
    @Unique private boolean dropper;
    @Unique private static boolean cerium$dropper;
    @Unique private static BlockSource cerium$blocksource;
    @Unique private static boolean cerium$spawnItemReturn;


    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
        Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
        Position position = DispenserBlock.getDispensePosition(blockSource);
        ItemStack itemStack2 = itemStack.split(1);
        // CraftBukkit start
        cerium$dropper = dropper;
        cerium$blocksource = blockSource;
        spawnItem(blockSource.level(), itemStack2, 6, direction, position);
        if (!cerium$spawnItemReturn) {
            itemStack.grow(1);
        }
        // CraftBukkit end
        return itemStack;
    }

    @Unique
    private static ItemEntity prepareItem(Level level, ItemStack itemStack, int i, Direction direction, Position position) {
        double d = position.x();
        double e = position.y();
        double f = position.z();
        if (direction.getAxis() == Direction.Axis.Y) {
            e -= 0.125;
        } else {
            e -= 0.15625;
        }

        ItemEntity itemEntity = new ItemEntity(level, d, e, f, itemStack);
        double g = level.random.nextDouble() * 0.1 + 0.2;
        itemEntity.setDeltaMovement(level.random.triangle((double)direction.getStepX() * g, 0.0172275 * (double)i), level.random.triangle(0.2, 0.0172275 * (double)i), level.random.triangle((double)direction.getStepZ() * g, 0.0172275 * (double)i));
        return itemEntity;
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static void spawnItem(Level level, ItemStack itemStack, int i, Direction direction, Position position) {
        // CraftBukkit start
        if (itemStack.isEmpty()) {
            cerium$spawnItemReturn = true;
            return;
        }
        ItemEntity entityitem = prepareItem(level, itemStack, i, direction, position);

        org.bukkit.block.Block block = CraftBlock.at(level, cerium$blocksource.pos());
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack);

        BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), CraftVector.toBukkit(entityitem.getDeltaMovement()));
        if (!DispenserBlock.eventFired) {
            level.getCraftServer().getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            cerium$spawnItemReturn = false;
            return;
        }

        entityitem.setItem(CraftItemStack.asNMSCopy(event.getItem()));
        entityitem.setDeltaMovement(CraftVector.toNMS(event.getVelocity()));

        if (!cerium$dropper && !event.getItem().getType().equals(craftItem.getType())) {
            // Chain to handler for new item
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
            if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior.getClass() != DefaultDispenseItemBehavior.class) {
                idispensebehavior.dispense(cerium$blocksource, eventStack);
            } else {
                level.addFreshEntity(entityitem);
            }
            cerium$spawnItemReturn = false;
            return;
        }

        level.addFreshEntity(entityitem);

        cerium$spawnItemReturn = true;
        return;
        // CraftBukkit end
    }
}
