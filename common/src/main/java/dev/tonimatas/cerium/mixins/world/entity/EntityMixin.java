package dev.tonimatas.cerium.mixins.world.entity;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tonimatas.cerium.bridge.world.entity.EntityBridge;
import net.minecraft.BlockUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftPortalEvent;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.projectiles.ProjectileSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityBridge {
    @Shadow private Level level;
    @Shadow @Final public static int TOTAL_AIR_SUPPLY;
    @Shadow private float yRot;
    @Shadow public abstract double getX();
    @Shadow public abstract double getZ();
    @Shadow public abstract Pose getPose();
    @Shadow public abstract String getScoreboardName();
    @Shadow protected abstract void handleNetherPortal();
    @Shadow public abstract Level level();
    @Shadow private int remainingFireTicks;
    @Shadow public abstract boolean hurt(DamageSource arg, float f);
    @Shadow public abstract DamageSources damageSources();

    @Shadow @Final public RandomSource random;
    @Shadow public abstract void playSound(SoundEvent arg, float f, float g);
    @Shadow public abstract void setRemainingFireTicks(int i);
    @Shadow public boolean horizontalCollision;
    @Shadow public abstract double getY();
    @Shadow protected abstract SoundEvent getSwimSound();
    @Shadow protected abstract SoundEvent getSwimSplashSound();
    @Shadow protected abstract SoundEvent getSwimHighSpeedSplashSound();
    @Shadow public abstract boolean isPushable();
    @Shadow @Nullable private Entity.RemovalReason removalReason;
    @Shadow @Nullable public abstract String getEncodeId();
    @Shadow protected abstract Vec3 collide(Vec3 arg);
    @Shadow public abstract void fillCrashReportCategory(CrashReportCategory arg);
    @Shadow public abstract List<Entity> getPassengers();
    @Shadow protected abstract void addAdditionalSaveData(CompoundTag arg);
    @Shadow public abstract boolean isVehicle();
    @Shadow @Final private Set<String> tags;
    @Shadow public boolean hasVisualFire;
    @Shadow public abstract int getTicksFrozen();
    @Shadow public abstract boolean hasGlowingTag();
    @Shadow public abstract boolean isNoGravity();
    @Shadow public abstract boolean isSilent();
    @Shadow public abstract boolean isCustomNameVisible();
    @Shadow @Nullable public abstract Component getCustomName();
    @Shadow public abstract UUID getUUID();
    @Shadow public int portalCooldown;
    @Shadow private boolean invulnerable;
    @Shadow public abstract boolean onGround();
    @Shadow public abstract int getAirSupply();
    @Shadow public float fallDistance;
    @Shadow public abstract float getYRot();
    @Shadow public abstract float getXRot();
    @Shadow protected abstract ListTag newFloatList(float... fs);
    @Shadow protected abstract ListTag newDoubleList(double... ds);
    @Shadow public abstract Vec3 getDeltaMovement();
    @Shadow @Nullable private Entity vehicle;
    @Shadow private float xRot;

    @Shadow public abstract int getMaxAirSupply();

    @Shadow public abstract void setInvisible(boolean bl);

    @Shadow protected abstract void removePassenger(Entity entity);

    @Shadow @org.jetbrains.annotations.Nullable public abstract Entity changeDimension(ServerLevel serverLevel);

    @Shadow public abstract boolean isSwimming();

    @Shadow public abstract void setSharedFlag(int i, boolean bl);

    @Shadow @Final protected SynchedEntityData entityData;

    @Shadow public abstract boolean fireImmune();

    @Shadow public abstract boolean isRemoved();

    @Shadow @org.jetbrains.annotations.Nullable protected abstract PortalInfo findDimensionEntryPoint(ServerLevel serverLevel);

    @Shadow public abstract void moveTo(Vec3 vec3);

    @Shadow public abstract void moveTo(double d, double e, double f, float g, float h);

    @Shadow public abstract void setDeltaMovement(Vec3 vec3);

    @Shadow public abstract void unRide();

    @Shadow public abstract EntityType<?> getType();

    @Shadow protected abstract void removeAfterChangingDimensions();

    @Shadow public abstract void load(CompoundTag compoundTag);

    @Shadow protected abstract Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel serverLevel, BlockPos blockPos, boolean bl, WorldBorder worldBorder);

    @Shadow protected BlockPos portalEntrancePos;

    @Shadow protected abstract Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle foundRectangle);

    @Shadow public abstract boolean teleportTo(ServerLevel serverLevel, double d, double e, double f, Set<RelativeMovement> set, float g, float h);

    @Shadow private AABB bb;
    @Unique private static final int CURRENT_LEVEL = 2;

    @Unique
    static boolean isLevelAtLeast(CompoundTag tag, int level) {
        return tag.contains("Bukkit.updateLevel") && tag.getInt("Bukkit.updateLevel") >= level;
    }

    @Unique private CraftEntity bukkitEntity;

    @Override
    public void bridge$setBukkitEntity(CraftEntity craftEntity) {
        this.bukkitEntity = craftEntity;
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = CraftEntity.getEntity(level.getCraftServer(), this);
        }
        return bukkitEntity;
    }

    public CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return getBukkitEntity();
    }

    @Override
    public int getDefaultMaxAirSupply() {
        return TOTAL_AIR_SUPPLY;
    }

    @Unique public boolean persist = true;
    @Override
    public boolean bridge$getPersist() {
        return this.persist;
    }

    @Override
    public void bridge$setPersist(boolean value) {
        this.persist = value;
    }

    @Unique public boolean visibleByDefault = true;

    @Override
    public boolean bridge$getVisibleByDefault() {
        return this.visibleByDefault;
    }

    @Override
    public void bridge$setVisibleByDefault(boolean value) {
        this.visibleByDefault = value;
    }

    @Unique public boolean valid;

    @Override
    public boolean bridge$getValid() {
        return this.valid;
    }

    @Unique public boolean inWorld = false;

    @Override
    public boolean bridge$getInWorld() {
        return this.inWorld;
    }

    @Unique public boolean generation;

    @Override
    public boolean bridge$getGeneration() {
        return this.generation;
    }

    @Override
    public void bridge$setGeneration(boolean generation) {
        this.generation = generation;
    }

    @Unique public int maxAirTicks = getDefaultMaxAirSupply();

    @Override
    public int bridge$getMaxAirTicks() {
        return this.maxAirTicks;
    }

    @Override
    public void bridge$setMaxAirTicks(int value) {
        this.maxAirTicks = value;
    }

    @Unique public org.bukkit.projectiles.ProjectileSource projectileSource;

    @Override
    public ProjectileSource bridge$getProjectileSource() {
        return this.projectileSource;
    }

    @Override
    public void bridge$setProjectileSource(ProjectileSource value) {
        this.projectileSource = value;
    }

    @Unique public boolean lastDamageCancelled;

    @Override
    public void bridge$setLastDamageCancelled(boolean value) {
        this.lastDamageCancelled = value;
    }

    @Unique public boolean persistentInvisibility = false;

    @Override
    public void bridge$setPersistentInvisibility(boolean value) {
        this.persistentInvisibility = value;
    }

    @Unique public BlockPos lastLavaContact;
    @Unique public boolean pluginRemoved = false;

    @Override
    public void bridge$setPluginRemoved(boolean value) {
        this.pluginRemoved = value;
    }

    @Override
    public float getBukkitYaw() {
        return this.yRot;
    }

    @Override
    public boolean isChunkLoaded() {
        return level.hasChunk((int) Math.floor(this.getX()) >> 4, (int) Math.floor(this.getZ()) >> 4);
    }

    @Inject(method = "setPose", at = @At(value = "HEAD"))
    private void cerium$setPose(Pose pose, CallbackInfo ci) {
        if (pose == this.getPose()) {
            return;
        }
        this.level.getCraftServer().getPluginManager().callEvent(new EntityPoseChangeEvent(this.getBukkitEntity(), org.bukkit.entity.Pose.values()[pose.ordinal()]));
    }

    @Inject(method = "setRot", at = @At(value = "HEAD"))
    private void cerium$setRot(float f, float g, CallbackInfo ci) {
        if (Float.isNaN(f)) {
            f = 0;
        }

        if (f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY) {
            if ((Object) this instanceof ServerPlayer) {
                this.level.getCraftServer().getLogger().warning(this.getScoreboardName() + " was caught trying to crash the server with an invalid yaw");
                ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Infinite yaw (Hacking?)");
            }
            f = 0;
        }

        // pitch was sometimes set to NaN, so we need to set it back to 0
        if (Float.isNaN(g)) {
            g = 0;
        }

        if (g == Float.POSITIVE_INFINITY || g == Float.NEGATIVE_INFINITY) {
            if ((Object) this instanceof ServerPlayer) {
                this.level.getCraftServer().getLogger().warning(this.getScoreboardName() + " was caught trying to crash the server with an invalid pitch");
                ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Infinite pitch (Hacking?)");
            }
            g = 0;
        }
    }

    @Override
    public void postTick() {
        if (!((Object) this instanceof ServerPlayer)) {
            this.handleNetherPortal();
        }
    }

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;handleNetherPortal()V"))
    public void cerium$baseTick(Entity entity) {
        if ((Object) this instanceof ServerPlayer) this.handleNetherPortal();
    }

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/entity/Entity;isInLava()Z"))
    private boolean cerium$resetLava(Entity instance) {
        var ret = instance.isInLava();
        if (!ret) {
            this.lastLavaContact = null;
        }
        return ret;
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void lavaHurt() {
        if ((Object) this instanceof LivingEntityMixin && remainingFireTicks <= 0) {
            // not on fire yet
            Block damager = (lastLavaContact == null) ? null : CraftBlock.at(level, lastLavaContact);
            org.bukkit.entity.Entity damagee = this.getBukkitEntity();
            EntityCombustEvent combustEvent = new org.bukkit.event.entity.EntityCombustByBlockEvent(damager, damagee, 15);
            this.level.getCraftServer().getPluginManager().callEvent(combustEvent);

            if (!combustEvent.isCancelled()) {
                this.setSecondsOnFire(combustEvent.getDuration(), false);
            }
        } else {
            this.setSecondsOnFire(15, false);
        }

        CraftEventFactory.blockDamage = (lastLavaContact) == null ? null : CraftBlock.at(level, lastLavaContact);
        if (this.hurt(this.damageSources().lava(), 4.0F)) {
            this.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
        }

        CraftEventFactory.blockDamage = null;
    }

    @Unique private boolean cerium$setSecondsOnFire$callEvent;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void setSecondsOnFire(int i) {
        if (cerium$setSecondsOnFire$callEvent) {
            EntityCombustEvent event = new EntityCombustEvent(this.getBukkitEntity(), i);
            this.level.getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            i = event.getDuration();
        }

        int j = i * 20;
        if ((Object) this instanceof LivingEntityMixin) {
            j = ProtectionEnchantment.getFireAfterDampener((LivingEntityMixin) (Object) this, j);
        }

        if (this.remainingFireTicks < j) {
            this.setRemainingFireTicks(j);
        }

    }

    @Override
    public void setSecondsOnFire(int i, boolean callEvent) {
        cerium$setSecondsOnFire$callEvent = callEvent;
        this.setSecondsOnFire(i);
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;onGround()Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void cerium$move(MoverType moverType, Vec3 vec3, CallbackInfo ci, @Local Vec3 vec32) {
        if (horizontalCollision && getBukkitEntity() instanceof Vehicle) {
            Vehicle vehicle = (Vehicle) this.getBukkitEntity();
            org.bukkit.block.Block bl = this.level.getWorld().getBlockAt(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));

            if (vec3.x > vec32.x) {
                bl = bl.getRelative(BlockFace.EAST);
            } else if (vec3.x < vec32.x) {
                bl = bl.getRelative(BlockFace.WEST);
            } else if (vec3.z > vec32.z) {
                bl = bl.getRelative(BlockFace.SOUTH);
            } else if (vec3.z < vec32.z) {
                bl = bl.getRelative(BlockFace.NORTH);
            }

            if (!bl.getType().isAir()) {
                VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, bl);
                level.getCraftServer().getPluginManager().callEvent(event);
            }
        }
    }

    @Override
    public SoundEvent getSwimSound0() {
        return this.getSwimSound();
    }

    @Override
    public SoundEvent getSwimSplashSound0() {
        return this.getSwimSplashSound();
    }

    @Override
    public SoundEvent getSwimHighSpeedSplashSound0() {
        return this.getSwimHighSpeedSplashSound();
    }

    @Inject(method = "absMoveTo(DDD)V", at = @At(value = "RETURN"))
    private void cerium$absMoveTo(double d, double e, double f, CallbackInfo ci) {
        if (valid) level.getChunk((int) Math.floor(this.getX()) >> 4, (int) Math.floor(this.getZ()) >> 4);
    }

    @Override
    public boolean canCollideWithBukkit(Entity entity) {
        return this.isPushable();
    }

    @Unique private boolean cerium$saveAsPassenger$includeAll = false;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public boolean saveAsPassenger(CompoundTag compoundTag) {
        if (this.removalReason != null && !this.removalReason.shouldSave()) {
            return false;
        } else {
            String string = this.getEncodeId();
            if (!this.persist || string == null) {
                return false;
            } else {
                compoundTag.putString("id", string);
                this.cerium$saveWithoutId$includeAll = cerium$saveAsPassenger$includeAll;
                this.saveWithoutId(compoundTag);
                return true;
            }
        }
    }

    @Override
    public boolean saveAsPassenger(CompoundTag compoundTag, boolean includeAll) {
        this.cerium$saveAsPassenger$includeAll = includeAll;
        return this.saveAsPassenger(compoundTag);
    }

    @Unique private boolean cerium$saveWithoutId$includeAll = false;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public CompoundTag saveWithoutId(CompoundTag compoundTag) {
        try {
            if (cerium$saveWithoutId$includeAll) {
                if (this.vehicle != null) {
                    compoundTag.put("Pos", this.newDoubleList(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
                } else {
                    compoundTag.put("Pos", this.newDoubleList(this.getX(), this.getY(), this.getZ()));
                }
            }

            Vec3 vec3 = this.getDeltaMovement();
            compoundTag.put("Motion", this.newDoubleList(vec3.x, vec3.y, vec3.z));
            if (Float.isNaN(this.yRot)) {
                this.yRot = 0;
            }

            if (Float.isNaN(this.xRot)) {
                this.xRot = 0;
            }
            compoundTag.put("Rotation", this.newFloatList(this.getYRot(), this.getXRot()));
            compoundTag.putFloat("FallDistance", this.fallDistance);
            compoundTag.putShort("Fire", (short)this.remainingFireTicks);
            compoundTag.putShort("Air", (short)this.getAirSupply());
            compoundTag.putBoolean("OnGround", this.onGround());
            compoundTag.putBoolean("Invulnerable", this.invulnerable);
            compoundTag.putInt("PortalCooldown", this.portalCooldown);

            if (cerium$saveWithoutId$includeAll) {
                compoundTag.putUUID("UUID", this.getUUID());
                compoundTag.putLong("WorldUUIDLeast", ((ServerLevel) this.level).getWorld().getUID().getLeastSignificantBits());
                compoundTag.putLong("WorldUUIDMost", ((ServerLevel) this.level).getWorld().getUID().getMostSignificantBits());
            }

            compoundTag.putInt("Bukkit.updateLevel", CURRENT_LEVEL);

            if (!this.persist) {
                compoundTag.putBoolean("Bukkit.persist", this.persist);
            }

            if (!this.visibleByDefault) {
                compoundTag.putBoolean("Bukkit.visibleByDefault", this.visibleByDefault);
            }

            if (this.persistentInvisibility) {
                compoundTag.putBoolean("Bukkit.invisible", this.persistentInvisibility);
            }

            if (maxAirTicks != getDefaultMaxAirSupply()) {
                compoundTag.putInt("Bukkit.MaxAirSupply", getMaxAirSupply());
            }

            Component component = this.getCustomName();
            if (component != null) {
                compoundTag.putString("CustomName", Component.Serializer.toJson(component));
            }

            if (this.isCustomNameVisible()) {
                compoundTag.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }

            if (this.isSilent()) {
                compoundTag.putBoolean("Silent", this.isSilent());
            }

            if (this.isNoGravity()) {
                compoundTag.putBoolean("NoGravity", this.isNoGravity());
            }

            if (this.hasGlowingTag()) {
                compoundTag.putBoolean("Glowing", true);
            }

            int i = this.getTicksFrozen();
            if (i > 0) {
                compoundTag.putInt("TicksFrozen", this.getTicksFrozen());
            }

            if (this.hasVisualFire) {
                compoundTag.putBoolean("HasVisualFire", this.hasVisualFire);
            }

            ListTag listTag;
            Iterator var6;
            if (!this.tags.isEmpty()) {
                listTag = new ListTag();
                var6 = this.tags.iterator();

                while(var6.hasNext()) {
                    String string = (String)var6.next();
                    listTag.add(StringTag.valueOf(string));
                }

                compoundTag.put("Tags", listTag);
            }

            this.addAdditionalSaveData(compoundTag);
            if (this.isVehicle()) {
                listTag = new ListTag();
                var6 = this.getPassengers().iterator();

                while(var6.hasNext()) {
                    Entity entity = (Entity)var6.next();
                    CompoundTag compoundTag2 = new CompoundTag();
                    this.cerium$saveAsPassenger$includeAll = cerium$saveWithoutId$includeAll;
                    if (entity.saveAsPassenger(compoundTag2)) {
                        listTag.add(compoundTag2);
                    }
                }

                if (!listTag.isEmpty()) {
                    compoundTag.put("Passengers", listTag);
                }
            }

            if (this.bukkitEntity != null) {
                this.bukkitEntity.storeBukkitValues(compoundTag);
            }

            return compoundTag;
        } catch (Throwable var9) {
            CrashReport crashReport = CrashReport.forThrowable(var9, "Saving entity NBT");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Entity being saved");
            this.fillCrashReportCategory(crashReportCategory);
            throw new ReportedException(crashReport);
        }
    }

    @Override
    public CompoundTag saveWithoutId(CompoundTag compoundTag, boolean includeAll) {
        this.cerium$saveWithoutId$includeAll = includeAll;
        return this.saveWithoutId(compoundTag);
    }

    @Inject(method = "load", at = @At(value = "RETURN"))
    private void cerium$load(CompoundTag compoundTag, CallbackInfo ci) {
        this.persist = !compoundTag.contains("Bukkit.persist") || compoundTag.getBoolean("Bukkit.persist");
        this.visibleByDefault = !compoundTag.contains("Bukkit.visibleByDefault") || compoundTag.getBoolean("Bukkit.visibleByDefault");
        // SPIGOT-6907: re-implement LivingEntity#setMaximumAir()
        if (compoundTag.contains("Bukkit.MaxAirSupply")) {
            maxAirTicks = compoundTag.getInt("Bukkit.MaxAirSupply");
        }
        // CraftBukkit end

        // CraftBukkit start - Reset world
        if ((Object) this instanceof ServerPlayer) {
            Server server = Bukkit.getServer();
            org.bukkit.World bworld = null;

            // TODO: Remove World related checks, replaced with WorldUID
            String worldName = compoundTag.getString("world");

            if (compoundTag.contains("WorldUUIDMost") && compoundTag.contains("WorldUUIDLeast")) {
                UUID uid = new UUID(compoundTag.getLong("WorldUUIDMost"), compoundTag.getLong("WorldUUIDLeast"));
                bworld = server.getWorld(uid);
            } else {
                bworld = server.getWorld(worldName);
            }

            if (bworld == null) {
                bworld = ((CraftServer) server).getServer().getLevel(Level.OVERWORLD).getWorld();
            }

            ((ServerPlayer) (Object) this).setLevel(bworld == null ? null : ((CraftWorld) bworld).getHandle());
        }
        this.getBukkitEntity().readBukkitValues(compoundTag);
        if (compoundTag.contains("Bukkit.invisible")) {
            boolean bukkitInvisible = compoundTag.getBoolean("Bukkit.invisible");
            this.setInvisible(bukkitInvisible);
            this.persistentInvisibility = bukkitInvisible;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag, boolean includeAll) {
        addAdditionalSaveData(compoundTag);
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public ItemEntity spawnAtLocation(ItemStack itemStack, float f) {
        if (itemStack.isEmpty()) {
            return null;
        } else if (this.level().isClientSide) {
            return null;
        } else {
            if ((Object) this instanceof LivingEntityMixin && !((LivingEntityMixin) (Object) this).forceDrops) {
                ((LivingEntityMixin) (Object) this).drops.add(CraftItemStack.asBukkitCopy(itemStack));
                return null;
            }
            ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY() + (double)f, this.getZ(), itemStack);
            itemEntity.setDefaultPickUpDelay();
            EntityDropItemEvent event = new EntityDropItemEvent(this.getBukkitEntity(), (org.bukkit.entity.Item) ((EntityBridge) itemEntity).getBukkitEntity());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return null;
            }
            this.level().addFreshEntity(itemEntity);
            return itemEntity;
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z", shift = At.Shift.BEFORE), cancellable = true)
    private void cerium$startRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (((EntityBridge) entity).getBukkitEntity() instanceof Vehicle && ((EntityBridge) (Object) this).getBukkitEntity() instanceof org.bukkit.entity.LivingEntity) {
            VehicleEnterEvent event = new VehicleEnterEvent((Vehicle) ((EntityBridge) entity).getBukkitEntity(), this.getBukkitEntity());

            if (this.valid) {
                Bukkit.getPluginManager().callEvent(event);
            }
            if (event.isCancelled()) {
                cir.setReturnValue(false);
            }
        }
    }

    @Redirect(method = "removeVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;removePassenger(Lnet/minecraft/world/entity/Entity;)V"))
    private void cerium$removeVehicle(Entity instance, Entity arg) {
        if (!((EntityBridge) instance).removePassengerBukkit((Entity) (Object) this)) this.vehicle = instance; // CraftBukkit
    }

    @Unique private AtomicBoolean cerium$removePassenger$return = new AtomicBoolean();

    @Inject(method = "removePassenger", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;size()I", shift = At.Shift.BEFORE), cancellable = true)
    private void cerium$removePassenger(Entity entity, CallbackInfo ci) {
        CraftEntity craft = (CraftEntity) ((EntityBridge) entity).getBukkitEntity().getVehicle();
        Entity orig = craft == null ? null : craft.getHandle();
        if (getBukkitEntity() instanceof Vehicle && ((EntityBridge) entity).getBukkitEntity() instanceof org.bukkit.entity.LivingEntity) {
            VehicleExitEvent event = new VehicleExitEvent((Vehicle) getBukkitEntity(), (org.bukkit.entity.LivingEntity) ((EntityBridge) entity).getBukkitEntity());
            // Suppress during worldgen
            if (this.valid) {
                Bukkit.getPluginManager().callEvent(event);
            }
            CraftEntity craftn = (CraftEntity) ((EntityBridge) entity).getBukkitEntity().getVehicle();
            Entity n = craftn == null ? null : craftn.getHandle();
            if (event.isCancelled() || n != orig) {
                cerium$removePassenger$return.set(false);
                ci.cancel();
            }
        }
    }

    @Inject(method = "removePassenger", at = @At(value = "RETURN"))
    private void cerium$removePassenger$return(Entity entity, CallbackInfo ci) {
        cerium$removePassenger$return.set(true);
    }

    @Override
    public boolean removePassengerBukkit(Entity entity) {
        this.removePassenger(entity);
        return cerium$removePassenger$return.getAndSet(true);
    }

    @Redirect(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;"))
    private Entity cerium$handleNetherPortal(Entity instance, ServerLevel serverLevel) {
        if ((Object) this instanceof ServerPlayer) {
            return ((ServerPlayer) (Object) this).changeDimension(serverLevel, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
        } else {
            return this.changeDimension(serverLevel);
        }
    }

    @Inject(method = "setSwimming", at = @At(value = "HEAD"), cancellable = true)
    private void cerium$setSwimming(boolean bl, CallbackInfo ci) {
        if (valid && this.isSwimming() != bl && (Object) this instanceof LivingEntityMixin) {
            if (CraftEventFactory.callToggleSwimEvent((LivingEntityMixin) (Object) this, bl).isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Redirect(method = "setInvisible", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setSharedFlag(IZ)V"))
    private void cerium$handleNetherPortal(Entity instance, int i, boolean bl) {
        if (!this.persistentInvisibility) { // Prevent Minecraft from removing our invisibility flag
            this.setSharedFlag(i, bl);
        }
    }

    @ModifyConstant(method = "getMaxAirSupply", constant = @Constant(intValue = 300))
    private int cerium$getMaxAirSupply(int constant) {
        return maxAirTicks;
    }

    @Inject(method = "setAirSupply", at = @At(value = "HEAD"), cancellable = true)
    private void cerium$setAirSupply(int i, CallbackInfo ci) {
        EntityAirChangeEvent event = new EntityAirChangeEvent(this.getBukkitEntity(), i);
        if (this.valid) {
            event.getEntity().getServer().getPluginManager().callEvent(event);
        }
        if (event.isCancelled() && this.getAirSupply() != i) {
            this.entityData.markDirty(Entity.DATA_AIR_SUPPLY_ID);
            ci.cancel();
        }
        this.entityData.set(Entity.DATA_AIR_SUPPLY_ID, event.getAmount());
        ci.cancel();
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
        this.setRemainingFireTicks(this.remainingFireTicks + 1);
        final org.bukkit.entity.Entity thisBukkitEntity = this.getBukkitEntity();
        final org.bukkit.entity.Entity stormBukkitEntity = ((EntityBridge) lightningBolt).getBukkitEntity();
        final PluginManager pluginManager = Bukkit.getPluginManager();

        if (this.remainingFireTicks == 0) {
            EntityCombustByEntityEvent entityCombustEvent = new EntityCombustByEntityEvent(stormBukkitEntity, thisBukkitEntity, 8);
            pluginManager.callEvent(entityCombustEvent);
            if (!entityCombustEvent.isCancelled()) {
                this.setSecondsOnFire(entityCombustEvent.getDuration(), false);
            }
        }

        if (thisBukkitEntity instanceof Hanging) {
            HangingBreakByEntityEvent hangingEvent = new HangingBreakByEntityEvent((Hanging) thisBukkitEntity, stormBukkitEntity);
            pluginManager.callEvent(hangingEvent);

            if (hangingEvent.isCancelled()) {
                return;
            }
        }

        if (this.fireImmune()) {
            return;
        }
        CraftEventFactory.entityDamage = lightningBolt;
        if (!this.hurt(this.damageSources().lightningBolt(), 5.0F)) {
            CraftEventFactory.entityDamage = null;
        }
    }

    @Unique public AtomicReference<Vec3> cerium$teleportTo$location = new AtomicReference<>(null);

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public Entity changeDimension(ServerLevel serverLevel) {
        if (this.level() instanceof ServerLevel && !this.isRemoved()) {
            this.level().getProfiler().push("changeDimension");
            if (serverLevel == null) {
                return null;
            }
            this.level().getProfiler().push("reposition");
            Vec3 location = cerium$teleportTo$location.getAndSet(null);
            PortalInfo portalInfo = (location == null) ? this.findDimensionEntryPoint(serverLevel) : new PortalInfo(new Vec3(location.x(), location.y(), location.z()), Vec3.ZERO, this.yRot, this.xRot, serverLevel, null);
            if (portalInfo == null) {
                return null;
            } else {
                serverLevel = portalInfo.world;
                if (serverLevel == level) {
                    moveTo(portalInfo.pos.x, portalInfo.pos.y, portalInfo.pos.z, portalInfo.yRot, portalInfo.xRot);
                    setDeltaMovement(portalInfo.speed);
                    return (Entity) (Object) this;
                }
                this.unRide();
                this.level().getProfiler().popPush("reloading");
                Entity entity = this.getType().create(serverLevel);
                if (entity != null) {
                    entity.restoreFrom((Entity) (Object) this);
                    entity.moveTo(portalInfo.pos.x, portalInfo.pos.y, portalInfo.pos.z, portalInfo.yRot, entity.getXRot());
                    entity.setDeltaMovement(portalInfo.speed);
                    if (this.inWorld) {
                        serverLevel.addDuringTeleport(entity);
                        if (serverLevel.dimension() == Level.END) {
                            serverLevel.makeObsidianPlatform(serverLevel, (Entity) (Object) this);
                        }
                    }

                    this.getBukkitEntity().setHandle(entity);
                    ((EntityBridge) entity).bridge$setBukkitEntity(this.getBukkitEntity());

                    if ((Entity) (Object) this instanceof Mob) {
                        ((Mob) (Object) this).dropLeash(true, false);
                    }
                }

                this.removeAfterChangingDimensions();
                this.level().getProfiler().pop();
                ((ServerLevel)this.level()).resetEmptyTime();
                serverLevel.resetEmptyTime();
                this.level().getProfiler().pop();
                return entity;
            }
        } else {
            return null;
        }
    }

    @Override
    public Entity teleportTo(ServerLevel serverLevel, Vec3 location) {
        this.cerium$teleportTo$location.set(location);
        return changeDimension(serverLevel);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected PortalInfo findDimensionEntryPoint(ServerLevel serverLevel) {
        if (serverLevel == null) {
            return null;
        }
        boolean bl = this.level().getTypeKey() == LevelStem.END && serverLevel.getTypeKey() == LevelStem.OVERWORLD;
        boolean bl2 = serverLevel.getTypeKey() == LevelStem.END;
        if (!bl && !bl2) {
            boolean bl3 = serverLevel.getTypeKey() == LevelStem.NETHER;
            if (this.level().getTypeKey() != LevelStem.NETHER && !bl3) {
                return null;
            } else {
                WorldBorder worldBorder = serverLevel.getWorldBorder();
                double d = DimensionType.getTeleportationScale(this.level().dimensionType(), serverLevel.dimensionType());
                BlockPos blockPos2 = worldBorder.clampToBounds(this.getX() * d, this.getY(), this.getZ() * d);
                CraftPortalEvent event = callPortalEvent((Entity) (Object) this, serverLevel, new Vec3(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()), PlayerTeleportEvent.TeleportCause.NETHER_PORTAL, bl3 ? 16 : 128, 16);
                if (event == null) {
                    return null;
                }
                final ServerLevel worldserverFinal = serverLevel = ((CraftWorld) event.getTo().getWorld()).getHandle();
                worldBorder = worldserverFinal.getWorldBorder();
                blockPos2 = worldBorder.clampToBounds(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ());
                return (PortalInfo) this.getExitPortal(serverLevel, blockPos2, bl3, worldBorder, event.getSearchRadius(), event.getCanCreatePortal(), event.getCreationRadius()).map((blockutil_rectangle) -> {
                    BlockState blockState = this.level().getBlockState(this.portalEntrancePos);
                    Direction.Axis axis;
                    Vec3 vec3;
                    if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
                        axis = (Direction.Axis)blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
                        BlockUtil.FoundRectangle foundRectangle2 = BlockUtil.getLargestRectangleAround(this.portalEntrancePos, axis, 21, Direction.Axis.Y, 21, (blockPos) -> {
                            return this.level().getBlockState(blockPos) == blockState;
                        });
                        vec3 = this.getRelativePortalPosition(axis, foundRectangle2);
                    } else {
                        axis = Direction.Axis.X;
                        vec3 = new Vec3(0.5, 0.0, 0.0);
                    }

                    return PortalShape.createPortalInfo(worldserverFinal, blockutil_rectangle, axis, vec3, this, this.getDeltaMovement(), this.getYRot(), this.getXRot());
                }).orElse((Object)null);
            }
        } else {
            BlockPos blockPos;
            if (bl2) {
                blockPos = ServerLevel.END_SPAWN_POINT;
            } else {
                blockPos = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, serverLevel.getSharedSpawnPos());
            }

            CraftPortalEvent event = callPortalEvent((Entity) (Object) this, serverLevel, new Vec3(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D), PlayerTeleportEvent.TeleportCause.END_PORTAL, 0, 0);
            if (event == null) {
                return null;
            }

            return new PortalInfo(new Vec3(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ()), this.getDeltaMovement(), this.getYRot(), this.getXRot(), ((CraftWorld) event.getTo().getWorld()).getHandle(), event);
        }
    }

    @Override
    public CraftPortalEvent callPortalEvent(Entity entity, ServerLevel exitWorldServer, Vec3 exitPosition, PlayerTeleportEvent.TeleportCause cause, int searchRadius, int creationRadius) {
        org.bukkit.entity.Entity bukkitEntity = ((EntityBridge) entity).getBukkitEntity();
        Location enter = bukkitEntity.getLocation();
        Location exit = CraftLocation.toBukkit(exitPosition, exitWorldServer.getWorld());

        EntityPortalEvent event = new EntityPortalEvent(bukkitEntity, enter, exit, searchRadius);
        event.getEntity().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getTo() == null || event.getTo().getWorld() == null || !entity.isAlive()) {
            return null;
        }
        return new CraftPortalEvent(event);
    }

    @Override
    public Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel worldserver, BlockPos blockposition, boolean flag, WorldBorder worldborder, int searchRadius, boolean canCreatePortal, int createRadius) {
        return worldserver.getPortalForcer().findPortalAround(blockposition, worldborder, searchRadius);
    }

    @Override
    public boolean teleportTo(ServerLevel worldserver, double d0, double d1, double d2, Set<RelativeMovement> set, float f, float f1, PlayerTeleportEvent.TeleportCause cause) {
        return this.teleportTo(worldserver, d0, d1, d2, set, f, f1);
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addDuringTeleport(Lnet/minecraft/world/entity/Entity;)V"), cancellable = true)
    private void cerium$teleportTo(ServerLevel serverLevel, double d, double e, double f, Set<RelativeMovement> set, float g, float h, CallbackInfoReturnable<Boolean> cir, @Local Entity entity) {
        if (inWorld) {
            serverLevel.addDuringTeleport(entity);
        }
    }

    @Inject(method = "setBoundingBox", at = @At(value = "HEAD"))
    private void cerium$setBoundingBox(AABB aABB, CallbackInfo ci) {
        double minX = aABB.minX, minY = aABB.minY, minZ = aABB.minZ, maxX = aABB.maxX, maxY = aABB.maxY, maxZ = aABB.maxZ;
        double len = aABB.maxX - aABB.minX;
        if (len < 0) maxX = minX;
        if (len > 64) maxX = minX + 64.0;

        len = aABB.maxY - aABB.minY;
        if (len < 0) maxY = minY;
        if (len > 64) maxY = minY + 64.0;

        len = aABB.maxZ - aABB.minZ;
        if (len < 0) maxZ = minZ;
        if (len > 64) maxZ = minZ + 64.0;
        this.bb = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Redirect(method = "updateFluidHeightAndDoFluidPushing", remap = false, at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/level/material/FluidState;getFlow(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 cerium$updateFluidHeightAndDoFluidPushing(FluidState instance, BlockGetter blockGetter, BlockPos blockPos) {
        if (instance.getType().is(FluidTags.LAVA)) {
            lastLavaContact = blockPos.immutable();
        }
        return instance.getFlow(level, blockPos);
    }
}
