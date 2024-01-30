package dev.tonimatas.cerium.mixins.world.entity;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tonimatas.cerium.bridge.world.entity.EntityBridge;
import dev.tonimatas.cerium.bridge.world.entity.LivingEntityBridge;
import dev.tonimatas.cerium.bridge.world.level.LevelBridge;
import dev.tonimatas.cerium.util.CeriumClasses;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.attribute.CraftAttributeMap;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityBridge {
    @Shadow public abstract float getYHeadRot();

    @Shadow @Final private AttributeMap attributes;

    @Shadow @Nullable public abstract AttributeInstance getAttribute(Attribute attribute);

    @Shadow protected abstract void onEffectRemoved(MobEffectInstance arg);

    @Shadow @Final public Map<MobEffect, MobEffectInstance> activeEffects;

    @Shadow protected abstract void onEffectUpdated(MobEffectInstance arg, boolean bl, @Nullable Entity arg2);

    @Shadow private boolean effectsDirty;

    @Shadow protected abstract void updateInvisibilityStatus();

    @Shadow protected abstract void updateGlowingStatus();

    @Shadow public abstract boolean addEffect(MobEffectInstance arg);

    @Shadow public abstract boolean removeAllEffects();

    @Shadow protected abstract void onEffectAdded(MobEffectInstance arg, @Nullable Entity arg2);

    @Shadow public abstract boolean canBeAffected(MobEffectInstance arg);

    @Shadow public abstract void remove(Entity.RemovalReason arg);

    @Shadow public abstract boolean removeEffect(MobEffect arg);

    @Shadow public abstract float getHealth();

    @Shadow public abstract void setHealth(float f);

    @Shadow public abstract boolean canAttack(LivingEntity arg);

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
            return instance.sendParticles((ServerPlayer) (Object) this, new BlockParticleOption(ParticleTypes.BLOCK, blockState), ((LivingEntity) (Object) this).getX(), ((LivingEntity) (Object) this).getY(), ((LivingEntity) (Object) this).getZ(), i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, false);
        } else {
            return instance.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), e, f, g, i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
        }
        // CraftBukkit end
    }
    
    // TODO: @@ -672,13 +718,19 @@
    
    @Unique public AtomicBoolean cerium$atomicBoolean = new AtomicBoolean(false);
    @WrapWithCondition(method = "onEquipItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private boolean cerium$onEquipItem(Level instance, Player arg, double d, double e, double f, SoundEvent arg2, SoundSource arg3, float g, float h) {
        return cerium$atomicBoolean.getAndSet(true);
    }
    
    @Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    private void cerium$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        // CraftBukkit start
        if (compoundTag.contains("Bukkit.MaxHealth")) {
            Tag nbtbase = compoundTag.get("Bukkit.MaxHealth");
            if (nbtbase.getId() == 5) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(((FloatTag) nbtbase).getAsDouble());
            } else if (nbtbase.getId() == 3) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(((IntTag) nbtbase).getAsDouble());
            }
        }
        // CraftBukkit end
    }

    // CraftBukkit start
    @Unique private boolean isTickingEffects = false;
    @Unique private List<CeriumClasses.ProcessableEffect> effectsToProcess = Lists.newArrayList();
    // CraftBukkit end

    // TODO: Add forge and neoforge events
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    protected void tickEffects() {
        Iterator<MobEffect> iterator = this.activeEffects.keySet().iterator();
        
        isTickingEffects = true; // CraftBukkit

        try {
            while(iterator.hasNext()) {
                MobEffect mobEffect = (MobEffect)iterator.next();
                MobEffectInstance mobEffectInstance = (MobEffectInstance)this.activeEffects.get(mobEffect);
                if (!mobEffectInstance.tick(((LivingEntity) (Object) this), () -> {
                    this.onEffectUpdated(mobEffectInstance, true, (Entity)null);
                })) {
                    if (!((LivingEntity) (Object) this).level().isClientSide) {
                        // CraftBukkit start
                        EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent((LivingEntity) (Object) this, mobEffectInstance, null, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.EXPIRATION);
                        if (event.isCancelled()) {
                            continue;
                        }
                        // CraftBukkit end
                        iterator.remove();
                        this.onEffectRemoved(mobEffectInstance);
                    }
                } else if (mobEffectInstance.getDuration() % 600 == 0) {
                    this.onEffectUpdated(mobEffectInstance, false, (Entity)null);
                }
            }
        } catch (ConcurrentModificationException var11) {
        }

        // CraftBukkit start
        isTickingEffects = false;
        for (CeriumClasses.ProcessableEffect e : effectsToProcess) {
            if (e.effect != null) {
                cerium$addEffectCause(e.cause);
                addEffect(e.effect);
            } else {
                removeEffect(e.type, e.cause);
            }
        }
        effectsToProcess.clear();
        // CraftBukkit end

        if (this.effectsDirty) {
            if (!((LivingEntity) (Object) this).level().isClientSide) {
                this.updateInvisibilityStatus();
                this.updateGlowingStatus();
            }

            this.effectsDirty = false;
        }

        int i = (Integer) ((LivingEntity) (Object) this).entityData.get(LivingEntity.DATA_EFFECT_COLOR_ID);
        boolean bl = (Boolean) ((LivingEntity) (Object) this).entityData.get(LivingEntity.DATA_EFFECT_AMBIENCE_ID);
        if (i > 0) {
            boolean bl2;
            if (((LivingEntity) (Object) this).isInvisible()) {
                bl2 = ((LivingEntity) (Object) this).random.nextInt(15) == 0;
            } else {
                bl2 = ((LivingEntity) (Object) this).random.nextBoolean();
            }

            if (bl) {
                bl2 &= ((LivingEntity) (Object) this).random.nextInt(5) == 0;
            }

            if (bl2 && i > 0) {
                double d = (double)(i >> 16 & 255) / 255.0;
                double e = (double)(i >> 8 & 255) / 255.0;
                double f = (double)(i >> 0 & 255) / 255.0;
                ((LivingEntity) (Object) this).level().addParticle(bl ? ParticleTypes.AMBIENT_ENTITY_EFFECT : ParticleTypes.ENTITY_EFFECT, ((LivingEntity) (Object) this).getRandomX(0.5), ((LivingEntity) (Object) this).getRandomY(), ((LivingEntity) (Object) this).getRandomZ(0.5), d, e, f);
            }
        }

    }
    
    @Inject(method = "tickEffects", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V", shift = At.Shift.BEFORE), cancellable = true)
    private void cerium$tickEffects$event(CallbackInfo ci, @Local MobEffectInstance mobeffect) {
        
    }

    @Unique private final AtomicReference<EntityPotionEffectEvent.Cause> cerium$removeAllEffectCause = new AtomicReference<>(EntityPotionEffectEvent.Cause.UNKNOWN);

    @Override
    @Unique
    public void cerium$addRemoveAllEffects(EntityPotionEffectEvent.Cause cause) {
        this.cerium$removeAllEffectCause.set(cause);
    }
    
    @Unique
    public boolean removeAllEffects(LivingEntity instance, EntityPotionEffectEvent.Cause cause) {
        this.cerium$removeAllEffectCause.set(cause);
        return this.removeAllEffects();
    }
    
    @Redirect(method = "removeAllEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V"))
    private void cerium$removeAllEffects(LivingEntity instance, MobEffectInstance effect) {
        // CraftBukkit start
        EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent((LivingEntity) (Object) this, effect, null, cerium$removeAllEffectCause.getAndSet(EntityPotionEffectEvent.Cause.UNKNOWN), EntityPotionEffectEvent.Action.CLEARED);
        if (event.isCancelled()) {
            // TODO: continue;
        }
        this.onEffectRemoved(effect);
        // CraftBukkit end
    }

    // CraftBukkit start
    @Unique private AtomicReference<EntityPotionEffectEvent.Cause> cerium$addEffectCause = new AtomicReference<>(EntityPotionEffectEvent.Cause.UNKNOWN);
    
    @Override
    public void cerium$addEffectCause(EntityPotionEffectEvent.Cause cause) {
        cerium$addEffectCause.set(cause);
    }
    
    public boolean addEffect(MobEffectInstance mobeffect, EntityPotionEffectEvent.Cause cause) {
        return this.addEffect(mobeffect, (Entity) null, cause);
    }

    public boolean addEffect(MobEffectInstance mobeffect, @Nullable Entity entity, EntityPotionEffectEvent.Cause cause) {
        cerium$addEffectCause.set(cause);
        return this.addEffect(mobeffect, entity);
    }

    // TODO: Add forge and neoforge events
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public boolean addEffect(MobEffectInstance mobEffectInstance, @Nullable Entity entity) {
        EntityPotionEffectEvent.Cause cause = cerium$addEffectCause.getAndSet(EntityPotionEffectEvent.Cause.UNKNOWN); // Cerium
        
        if (isTickingEffects) {
            effectsToProcess.add(new CeriumClasses.ProcessableEffect(mobEffectInstance, cause));
            return true;
        }
        // CraftBukkit end
        
        if (!this.canBeAffected(mobEffectInstance)) {
            return false;
        } else {
            MobEffectInstance mobEffectInstance2 = (MobEffectInstance) this.activeEffects.get(mobEffectInstance.getEffect());
            boolean bl = false;

            // CraftBukkit start
            boolean override = false;
            if (mobEffectInstance2 != null) {
                override = new MobEffect(mobEffectInstance2).update(mobEffectInstance);
            }

            EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent((LivingEntity) (Object) this, mobEffectInstance2, mobEffectInstance, cause, override);
            if (event.isCancelled()) {
                return false;
            }
            // CraftBukkit end
            
            if (mobEffectInstance2 == null) {
                this.activeEffects.put(mobEffectInstance.getEffect(), mobEffectInstance);
                this.onEffectAdded(mobEffectInstance, entity);
                bl = true;
                // CraftBukkit start
            } else if (event.isOverride()) {
                mobEffectInstance2.update(mobEffectInstance);
                // CraftBukkit end
                this.onEffectUpdated(mobEffectInstance2, true, entity);
                bl = true;
            }

            mobEffectInstance.onEffectStarted((LivingEntity) (Object) this);
            return bl;
        }
    }
    
    @Unique public AtomicReference<EntityPotionEffectEvent.Cause> cerium$removeEffectCause = new AtomicReference<>(EntityPotionEffectEvent.Cause.UNKNOWN);

    @Override
    public void cerium$addRemoveEffectCause(EntityPotionEffectEvent.Cause cause) {
        cerium$removeEffectCause.set(cause);
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    @Nullable
    public MobEffectInstance removeEffectNoUpdate(@Nullable MobEffect mobEffect) {
        EntityPotionEffectEvent.Cause cause = cerium$removeEffectCause.getAndSet(EntityPotionEffectEvent.Cause.UNKNOWN);
        if (isTickingEffects) {
            effectsToProcess.add(new CeriumClasses.ProcessableEffect(mobEffect, cause));
            return null;
        }

        MobEffectInstance effect = this.activeEffects.get(mobEffect);
        if (effect == null) {
            return null;
        }

        EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent((LivingEntity) (Object) this, effect, null, cause);
        if (event.isCancelled()) {
            return null;
        }

        return (MobEffectInstance) this.activeEffects.remove(mobEffect);
    }
    
    @Unique
    @Nullable
    public MobEffectInstance c(@Nullable MobEffect mobEffect, EntityPotionEffectEvent.Cause cause) {
        cerium$removeEffectCause.set(cause);
        return removeEffectNoUpdate(mobEffect);
    }
    
    @Unique
    public boolean removeEffect(MobEffect mobeffectlist, EntityPotionEffectEvent.Cause cause) {
        cerium$removeEffectCause.set(cause);
        return removeEffect(mobeffectlist);
    }
    
    @Unique public AtomicReference<EntityRegainHealthEvent.RegainReason> cerium$regainReason = new AtomicReference<>(EntityRegainHealthEvent.RegainReason.CUSTOM);
    
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void heal(float f) {
        float g = this.getHealth();
        if (g > 0.0F) {
            EntityRegainHealthEvent event = new EntityRegainHealthEvent(this.getBukkitEntity(), f, cerium$regainReason.getAndSet(EntityRegainHealthEvent.RegainReason.CUSTOM));
            // Suppress during worldgen
            if (((EntityBridge) this).bridge$getValid()) {
                ((LevelBridge) ((LivingEntity) (Object) this).level()).getCraftServer().getPluginManager().callEvent(event);
            }

            if (!event.isCancelled()) {
                this.setHealth((float) (this.getHealth() + event.getAmount()));
            }
            // CraftBukkit end
        }
    }

    @Unique
    public void heal(float f, EntityRegainHealthEvent.RegainReason regainReason) {
        cerium$regainReason.set(regainReason);
        heal(f);
    }
    
    @Inject(method = "getHealth", at = @At(value = "HEAD"), cancellable = true)
    private void cerium$getHealth(CallbackInfoReturnable<Float> cir) {
        // CraftBukkit start - Use unscaled health
        if ((Object) this instanceof ServerPlayer) {
            cir.setReturnValue((float) ((ServerPlayer) (Object) this).getBukkitEntity().getHealth());
        }
        // CraftBukkit end
    }
    
    @Inject(method = "setHealth", at = @At(value = "HEAD"))
    private void cerium$setHealth(float f, CallbackInfo ci) {
        // CraftBukkit start - Handle scaled health
        if ((Object) this instanceof ServerPlayer) {
            CraftPlayer player = ((ServerPlayer) (Object) this).getBukkitEntity();
            // Squeeze
            if (f < 0.0F) {
                player.setRealHealth(0.0D);
            } else if (f > player.getMaxHealth()) {
                player.setRealHealth(player.getMaxHealth());
            } else {
                player.setRealHealth(f);
            }

            player.updateScaledHealth(false);
            return;
        }
        // CraftBukkit end
    }
}
