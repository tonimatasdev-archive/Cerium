package dev.tonimatas.cerium.mixins.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static boolean dispenseArmor(BlockSource blockSource, ItemStack itemStack) {
        BlockPos blockPos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
        List<LivingEntity> list = blockSource.level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPos), EntitySelector.NO_SPECTATORS.and(new EntitySelector.MobCanWearArmorEntitySelector(itemStack)));
        if (list.isEmpty()) {
            return false;
        } else {
            LivingEntity livingEntity = (LivingEntity)list.get(0);
            EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(itemStack);
            ItemStack itemStack2 = itemStack.split(1);
            // CraftBukkit start
            Level world = blockSource.level();
            org.bukkit.block.Block block = CraftBlock.at(world, blockSource.pos());
            CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack2);

            BlockDispenseArmorEvent event = new BlockDispenseArmorEvent(block, craftItem.clone(), (org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity) livingEntity.getBukkitEntity());
            if (!DispenserBlock.eventFired) {
                world.getCraftServer().getPluginManager().callEvent(event);
            }

            if (event.isCancelled()) {
                itemStack.grow(1);
                return false;
            }

            if (!event.getItem().equals(craftItem)) {
                itemStack.grow(1);
                // Chain to handler for new item
                ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
                if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != ArmorItem.DISPENSE_ITEM_BEHAVIOR) {
                    idispensebehavior.dispense(blockSource, eventStack);
                    return true;
                }
            }

            livingEntity.setItemSlot(equipmentSlot, CraftItemStack.asNMSCopy(event.getItem()));
            // CraftBukkit end
            if (livingEntity instanceof Mob) {
                ((Mob)livingEntity).setDropChance(equipmentSlot, 2.0F);
                ((Mob)livingEntity).setPersistenceRequired();
            }

            return true;
        }
    }
}
