package dev.tonimatas.cerium.mixins.world.item;

import dev.tonimatas.cerium.bridge.world.level.LevelBridge;
import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.bukkit.craftbukkit.v1_20_R3.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.entity.Hanging;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(LeadItem.class)
public class LeadItemMixin {
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/LeadItem;bindPlayerMobs(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/InteractionResult;"))
    private void cerium$useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        CeriumValues.hand = useOnContext.getHand(); // Cerium // CraftBukkit - Pass hand
    }
    
    
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static InteractionResult bindPlayerMobs(Player player, Level level, BlockPos blockPos) {
        LeashFenceKnotEntity leashFenceKnotEntity = null;
        boolean bl = false;
        double d = 7.0;
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        List<Mob> list = level.getEntitiesOfClass(Mob.class, new AABB((double)i - 7.0, (double)j - 7.0, (double)k - 7.0, (double)i + 7.0, (double)j + 7.0, (double)k + 7.0));
        Iterator var11 = list.iterator();

        while(var11.hasNext()) {
            Mob mob = (Mob)var11.next();
            if (mob.getLeashHolder() == player) {
                if (leashFenceKnotEntity == null) {
                    leashFenceKnotEntity = LeashFenceKnotEntity.getOrCreateKnot(level, blockPos);
                    // CraftBukkit start - fire HangingPlaceEvent
                    org.bukkit.inventory.EquipmentSlot hand = CraftEquipmentSlot.getHand(CeriumValues.hand);
                    HangingPlaceEvent event = new HangingPlaceEvent((Hanging) leashFenceKnotEntity.getBukkitEntity(), player != null ? (org.bukkit.entity.Player) player.getBukkitEntity() : null, ((LevelBridge) level).getWorld().getBlockAt(i, j, k), org.bukkit.block.BlockFace.SELF, hand);
                    ((LevelBridge) level).getCraftServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        leashFenceKnotEntity.discard();
                        return InteractionResult.PASS;
                    }
                    // CraftBukkit end
                    leashFenceKnotEntity.playPlacementSound();
                }

                // CraftBukkit start
                if (CraftEventFactory.callPlayerLeashEntityEvent(mob, leashFenceKnotEntity, player, CeriumValues.hand).isCancelled()) {
                    continue;
                }
                // CraftBukkit end
                
                mob.setLeashedTo(leashFenceKnotEntity, true);
                bl = true;
            }
        }

        if (bl) {
            level.gameEvent(GameEvent.BLOCK_ATTACH, blockPos, GameEvent.Context.of(player));
        }

        return bl ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    // CraftBukkit start
    @Unique
    private static InteractionResult bindPlayerMobs(Player entityhuman, Level world, BlockPos blockposition, InteractionHand interactionHand) {
        CeriumValues.hand = interactionHand;
        return bindPlayerMobs(entityhuman, world, blockposition);
    }
    // CraftBukkit end
}
