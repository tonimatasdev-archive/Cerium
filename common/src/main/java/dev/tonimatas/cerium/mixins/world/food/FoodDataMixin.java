package dev.tonimatas.cerium.mixins.world.food;

import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(FoodData.class)
public abstract class FoodDataMixin implements FoodSta {

    // @formatter:off
    @Shadow public int foodLevel;
    @Shadow public abstract void eat(int foodLevelIn, float foodSaturationModifier);
    @Shadow public float saturationLevel;
    @Shadow private int lastFoodLevel;
    // @formatter:on

    @Shadow public float exhaustionLevel;
    @Shadow private int tickTimer;

    @Shadow public abstract void addExhaustion(float f);

    @Unique private Player entityhuman;
    @Unique public int saturatedRegenRate = 10;
    @Unique public int unsaturatedRegenRate = 80;
    @Unique public int starvationRate = 80;

    @Unique
    public void cerium$constructor() {
        throw new RuntimeException();
    }

    @Unique
    public void cerium$constructor(Player playerEntity) {
        cerium$constructor();
        this.entityhuman = playerEntity;
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void eat(Item item, ItemStack itemStack) {
        if (item.isEdible()) {
            FoodProperties foodProperties = item.getFoodProperties();
            // CraftBukkit start
            int oldFoodLevel = foodLevel;
            this.eat(foodProperties.getNutrition(), foodProperties.getSaturationModifier());
            FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(entityhuman, foodProperties.getNutrition() + oldFoodLevel, itemStack);

            if (!event.isCancelled()) {
                this.eat(event.getFoodLevel() - oldFoodLevel, foodProperties.getSaturationModifier());
            }

            ((ServerPlayer) entityhuman).getBukkitEntity().sendHealthUpdate();
            // CraftBukkit end
        }

    }
    
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void tick(Player player) {
        Difficulty difficulty = player.level().getDifficulty();
        this.lastFoodLevel = this.foodLevel;
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        boolean bl = player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        if (bl && this.saturationLevel > 0.0F && player.isHurt() && this.foodLevel >= 20) {
            ++this.tickTimer;
            if (this.tickTimer >= this.saturatedRegenRate) { // CraftBukkit
                float f = Math.min(this.saturationLevel, 6.0F);
                player.heal(f / 6.0F, EntityRegainHealthEvent.RegainReason.SATIATED);
                // this.addExhaustion(f); CraftBukkit - EntityExhaustionEvent
                entityhuman.causeFoodExhaustion(f, org.bukkit.event.entity.EntityExhaustionEvent.ExhaustionReason.REGEN); // CraftBukkit - EntityExhaustionEvent
                this.tickTimer = 0;
            }
        } else if (bl && this.foodLevel >= 18 && player.isHurt()) {
            ++this.tickTimer;
            if (this.tickTimer >= this.unsaturatedRegenRate) { // CraftBukkit - add regen rate manipulation
                entityhuman.heal(1.0F, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.SATIATED); // CraftBukkit - added RegainReason
                // this.a(6.0F); CraftBukkit - EntityExhaustionEvent
                entityhuman.causeFoodExhaustion(6.0f, org.bukkit.event.entity.EntityExhaustionEvent.ExhaustionReason.REGEN); // CraftBukkit - EntityExhaustionEvent
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= this.starvationRate) { // CraftBukkit - add regen rate manipulation
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.hurt(player.damageSources().starve(), 1.0F);
                }

                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }

    }
}