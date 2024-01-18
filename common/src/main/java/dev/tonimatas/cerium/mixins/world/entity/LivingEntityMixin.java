package dev.tonimatas.cerium.mixins.world.entity;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tonimatas.cerium.bridge.world.entity.LivingEntityBridge;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.attribute.CraftAttributeMap;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityBridge {
    @Shadow public abstract float getYHeadRot();

    @Shadow @Final private AttributeMap attributes;

    @Shadow @Nullable public abstract AttributeInstance getAttribute(Attribute attribute);

    @Unique public int expToDrop;

    @Override
    public int cerium$getExpToDrop() {
        return expToDrop;
    }

    @Override
    public void cerium$setExpToDrop(int value) {
        this.expToDrop = value;
    }

    @Unique public boolean forceDrops;
    @Unique public ArrayList<ItemStack> drops = new ArrayList<org.bukkit.inventory.ItemStack>();
    @Unique public org.bukkit.craftbukkit.v1_20_R3.attribute.CraftAttributeMap craftAttributes;

    @Override
    public CraftAttributeMap cerium$getCraftAttributeMap() {
        return craftAttributes;
    }

    @Unique public boolean collides = true;

    @Override
    public boolean cerium$getCollides() {
        return this.collides;
    }

    @Override
    public void cerium$setCollides(boolean value) {
        this.collides = value;
    }

    @Unique public Set<UUID> collidableExemptions = new HashSet<>();

    @Override
    public Set<UUID> cerium$getCollidableExemptions() {
        return this.collidableExemptions;
    }

    @Unique public boolean bukkitPickUpLoot;
    
    @Unique
    public float getBukkitYaw() {
        return getYHeadRot();
    }
    
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private void cerium$init(LivingEntity instance, float f) {
        this.craftAttributes = new CraftAttributeMap(attributes);
        ((LivingEntity) (Object) this).entityData.set(LivingEntity.DATA_HEALTH_ID, (float) this.getAttribute(Attributes.MAX_HEALTH).getValue());
    }
    
    @Redirect(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    private <T extends ParticleOptions> int cerium$checkFallDamage(ServerLevel instance, T particleOptions, double d, double e, double f, int i, double g, double h, double j, double k, @Local BlockState blockState) {
        // CraftBukkit start - visiblity api
        if ((Object) this instanceof ServerPlayer) {
            instance.sendParticles((ServerPlayer) (Object) this, new BlockParticleOption(ParticleTypes.BLOCK, blockState), ((LivingEntity) (Object) this).getX(), ((LivingEntity) (Object) this).getY(), ((LivingEntity) (Object) this).getZ(), i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, false);
        } else {
            instance.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), e, f, g, i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
        }
        // CraftBukkit end
    }
}
