package dev.tonimatas.cerium.mixins.core.dispenser;

import dev.tonimatas.cerium.bridge.world.entity.EntityBridge;
import dev.tonimatas.cerium.util.DoubleNestedClass;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.craftbukkit.v1_20_R3.util.DummyGeneratorAccess;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(DispenseItemBehavior.class)
public interface DispenseItemBehaviorMixin {

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    static void bootStrap() {
        DispenserBlock.registerBehavior(Items.ARROW, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                Arrow arrow = new Arrow(level, position.x(), position.y(), position.z(), itemStack.copyWithCount(1));
                arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return arrow;
            }
        });
        DispenserBlock.registerBehavior(Items.TIPPED_ARROW, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                Arrow arrow = new Arrow(level, position.x(), position.y(), position.z(), itemStack.copyWithCount(1));
                arrow.setEffectsFromItem(itemStack);
                arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return arrow;
            }
        });
        DispenserBlock.registerBehavior(Items.SPECTRAL_ARROW, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                AbstractArrow abstractArrow = new SpectralArrow(level, position.x(), position.y(), position.z(), itemStack.copyWithCount(1));
                abstractArrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return abstractArrow;
            }
        });
        DispenserBlock.registerBehavior(Items.EGG, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return (Projectile) Util.make(new ThrownEgg(level, position.x(), position.y(), position.z()), (thrownEgg) -> {
                    thrownEgg.setItem(itemStack);
                });
            }
        });
        DispenserBlock.registerBehavior(Items.SNOWBALL, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return (Projectile)Util.make(new Snowball(level, position.x(), position.y(), position.z()), (snowball) -> {
                    snowball.setItem(itemStack);
                });
            }
        });
        DispenserBlock.registerBehavior(Items.EXPERIENCE_BOTTLE, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return (Projectile)Util.make(new ThrownExperienceBottle(level, position.x(), position.y(), position.z()), (thrownExperienceBottle) -> {
                    thrownExperienceBottle.setItem(itemStack);
                });
            }

            protected float getUncertainty() {
                return super.getUncertainty() * 0.5F;
            }

            protected float getPower() {
                return super.getPower() * 1.25F;
            }
        });
        DispenserBlock.registerBehavior(Items.SPLASH_POTION, new DispenseItemBehavior() {
            public ItemStack dispense(BlockSource blockSource, ItemStack itemStack) {
                return DoubleNestedClass.makeSplashPotionDispenseBehavior(itemStack).dispense(blockSource, itemStack);
            }
        });
        DispenserBlock.registerBehavior(Items.LINGERING_POTION, new DispenseItemBehavior() {
            public ItemStack dispense(BlockSource blockSource, ItemStack itemStack) {
                return DoubleNestedClass.makeLingeringPotionDispenseBehavior(itemStack).dispense(blockSource, itemStack);
            }
        });
        DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior() {
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
                EntityType<?> entityType = ((SpawnEggItem)itemStack.getItem()).getType(itemStack.getTag());

                // CraftBukkit start
                ServerLevel worldserver = blockSource.level();
                ItemStack itemstack1 = itemStack.split(1);
                org.bukkit.block.Block block = CraftBlock.at(worldserver, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                if (!DispenserBlock.eventFired) {
                    worldserver.getCraftServer().getPluginManager().callEvent(event);
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

                try {
                    entityType.spawn(blockSource.level(), itemStack, (Player)null, blockSource.pos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
                } catch (Exception var6) {
                    LOGGER.error("Error while dispensing spawn egg from dispenser at {}", blockSource.pos(), var6);
                    return ItemStack.EMPTY;
                }

                // itemStack.shrink(1); // Handled during event processing
                // CraftBukkit end
                blockSource.level().gameEvent((Entity)null, GameEvent.ENTITY_PLACE, blockSource.pos());
                return itemStack;
            }
        };
        Iterator var1 = SpawnEggItem.eggs().iterator();

        while(var1.hasNext()) {
            SpawnEggItem spawnEggItem = (SpawnEggItem)var1.next();
            DispenserBlock.registerBehavior(spawnEggItem, defaultDispenseItemBehavior);
        }

        DispenserBlock.registerBehavior(Items.ARMOR_STAND, new DefaultDispenseItemBehavior() {
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.pos().relative(direction);
                ServerLevel serverLevel = blockSource.level();
                // CraftBukkit start
                ItemStack itemstack1 = itemStack.split(1);
                org.bukkit.block.Block block = CraftBlock.at(serverLevel, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
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
                // CraftBukkit end
                Consumer<ArmorStand> consumer = EntityType.appendDefaultStackConfig((armorStandx) -> {
                    armorStandx.setYRot(direction.toYRot());
                }, serverLevel, itemStack, (Player)null);
                ArmorStand armorStand = (ArmorStand)EntityType.ARMOR_STAND.spawn(serverLevel, itemStack.getTag(), consumer, blockPos, MobSpawnType.DISPENSER, false, false);
                if (armorStand != null) {
                    // itemStack.shrink(1); // CraftBukkit - Handled during event processing
                }

                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(Items.SADDLE, new OptionalDispenseItemBehavior() {
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                BlockPos blockPos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
                List<LivingEntity> list = blockSource.level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPos), (livingEntity) -> {
                    if (!(livingEntity instanceof Saddleable saddleable)) {
                        return false;
                    } else {
                        return !saddleable.isSaddled() && saddleable.isSaddleable();
                    }
                });
                if (!list.isEmpty()) {
                    // CraftBukkit start
                    ItemStack itemstack1 = itemStack.split(1);
                    Level world = blockSource.level();
                    org.bukkit.block.Block block = CraftBlock.at(world, blockSource.pos());
                    CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                    BlockDispenseArmorEvent event = new BlockDispenseArmorEvent(block, craftItem.clone(), (org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity) ((EntityBridge) list.get(0)).getBukkitEntity());
                    if (!DispenserBlock.eventFired) {
                        world.getCraftServer().getPluginManager().callEvent(event);
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
                        if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != ArmorItem.DISPENSE_ITEM_BEHAVIOR) {
                            idispensebehavior.dispense(blockSource, eventStack);
                            return itemStack;
                        }
                    }
                    // CraftBukkit end
                    ((Saddleable)list.get(0)).equipSaddle(SoundSource.BLOCKS);
                    // itemStack.shrink(1); // CraftBukkit - handled above
                    this.setSuccess(true);
                    return itemStack;
                } else {
                    return super.execute(blockSource, itemStack);
                }
            }
        });
        DefaultDispenseItemBehavior defaultDispenseItemBehavior2 = new OptionalDispenseItemBehavior() {
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                BlockPos blockPos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
                List<AbstractHorse> list = blockSource.level().getEntitiesOfClass(AbstractHorse.class, new AABB(blockPos), (abstractHorsex) -> {
                    return abstractHorsex.isAlive() && abstractHorsex.canWearArmor();
                });
                Iterator var5 = list.iterator();

                AbstractHorse abstractHorse;
                do {
                    if (!var5.hasNext()) {
                        return super.execute(blockSource, itemStack);
                    }

                    abstractHorse = (AbstractHorse)var5.next();
                } while(!abstractHorse.isArmor(itemStack) || abstractHorse.isWearingArmor() || !abstractHorse.isTamed());

                // CraftBukkit start
                ItemStack itemstack1 = itemStack.split(1);
                Level world = blockSource.level();
                org.bukkit.block.Block block = CraftBlock.at(world, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseArmorEvent event = new BlockDispenseArmorEvent(block, craftItem.clone(), (org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity) ((EntityBridge) abstractHorse).getBukkitEntity());
                if (!DispenserBlock.eventFired) {
                    world.getCraftServer().getPluginManager().callEvent(event);
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
                    if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != ArmorItem.DISPENSE_ITEM_BEHAVIOR) {
                        idispensebehavior.dispense(blockSource, eventStack);
                        return itemStack;
                    }
                }

                abstractHorse.getSlot(401).set(CraftItemStack.asNMSCopy(event.getItem()));
                // CraftBukkit end
                this.setSuccess(true);
                return itemStack;
            }
        };
        DispenserBlock.registerBehavior(Items.LEATHER_HORSE_ARMOR, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.IRON_HORSE_ARMOR, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.GOLDEN_HORSE_ARMOR, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.DIAMOND_HORSE_ARMOR, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.WHITE_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.ORANGE_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.CYAN_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.BLUE_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.BROWN_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.BLACK_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.GRAY_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.GREEN_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.LIGHT_BLUE_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.LIGHT_GRAY_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.LIME_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.MAGENTA_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.PINK_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.PURPLE_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.RED_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.YELLOW_CARPET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.CHEST, new OptionalDispenseItemBehavior() {
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                BlockPos blockPos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
                List<AbstractChestedHorse> list = blockSource.level().getEntitiesOfClass(AbstractChestedHorse.class, new AABB(blockPos), (abstractChestedHorsex) -> {
                    return abstractChestedHorsex.isAlive() && !abstractChestedHorsex.hasChest();
                });
                Iterator var5 = list.iterator();

                AbstractChestedHorse abstractChestedHorse;
                do {
                    if (!var5.hasNext()) {
                        return super.execute(blockSource, itemStack);
                    }

                    abstractChestedHorse = (AbstractChestedHorse)var5.next();
                    // CraftBukkit start
                } while(!abstractChestedHorse.isTamed());

                ItemStack itemstack1 = itemStack.split(1);
                Level world = blockSource.level();
                org.bukkit.block.Block block = CraftBlock.at(world, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseArmorEvent event = new BlockDispenseArmorEvent(block, craftItem.clone(), (org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity) ((EntityBridge) abstractChestedHorse).getBukkitEntity());
                if (!DispenserBlock.eventFired) {
                    world.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemStack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != ArmorItem.DISPENSE_ITEM_BEHAVIOR) {
                        idispensebehavior.dispense(blockSource, eventStack);
                        return itemStack;
                    }
                }
                abstractChestedHorse.getSlot(499).set(CraftItemStack.asNMSCopy(event.getItem()));
                // CraftBukkit end

                // itemStack.shrink(1); // CraftBukkit - handled above
                this.setSuccess(true);
                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(Items.FIREWORK_ROCKET, new DefaultDispenseItemBehavior() {
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
                // CraftBukkit start
                ServerLevel worldserver = blockSource.level();
                ItemStack itemstack1 = itemStack.split(1);
                org.bukkit.block.Block block = CraftBlock.at(worldserver, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(direction.getStepX(), direction.getStepY(), enumdirection.getStepZ()));
                if (!DispenserBlock.eventFired) {
                    worldserver.getCraftServer().getPluginManager().callEvent(event);
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

                itemstack1 = CraftItemStack.asNMSCopy(event.getItem());
                Vec3 vec3 = DispenseItemBehavior.getEntityPokingOutOfBlockPos(blockSource, EntityType.FIREWORK_ROCKET, direction);
                FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(blockSource.level(), itemStack, vec3.x(), vec3.y(), vec3.z(), true);
                fireworkRocketEntity.shoot((double)direction.getStepX(), (double)direction.getStepY(), (double)direction.getStepZ(), 0.5F, 1.0F);
                blockSource.level().addFreshEntity(fireworkRocketEntity);
                // itemStack.shrink(1); // Handled during event processing
                // CraftBukkit end
                return itemStack;
            }

            protected void playSound(BlockSource blockSource) {
                blockSource.level().levelEvent(1004, blockSource.pos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.FIRE_CHARGE, new DefaultDispenseItemBehavior() {
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
                Position position = DispenserBlock.getDispensePosition(blockSource);
                double d = position.x() + (double)((float)direction.getStepX() * 0.3F);
                double e = position.y() + (double)((float)direction.getStepY() * 0.3F);
                double f = position.z() + (double)((float)direction.getStepZ() * 0.3F);
                Level level = blockSource.level();
                RandomSource randomSource = level.random;
                double g = randomSource.triangle((double)direction.getStepX(), 0.11485000000000001);
                double h = randomSource.triangle((double)direction.getStepY(), 0.11485000000000001);
                double i = randomSource.triangle((double)direction.getStepZ(), 0.11485000000000001);
                // CraftBukkit start
                ItemStack itemstack1 = itemStack.split(1);
                org.bukkit.block.Block block = CraftBlock.at(level, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(d3, d4, d5));
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

                //itemStack.shrink(1);
                SmallFireball entitysmallfireball = new SmallFireball(level, d, e, f, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ());
                entitysmallfireball.setItem(itemstack1);
                entitysmallfireball.projectileSource = new org.bukkit.craftbukkit.v1_20_R3.projectiles.CraftBlockProjectileSource(blockSource.blockEntity());

                level.addFreshEntity(entitysmallfireball);
                // itemStack.shrink(1); // Handled during event processing
                // CraftBukkit end
                return itemStack;
            }

            protected void playSound(BlockSource blockSource) {
                blockSource.level().levelEvent(1018, blockSource.pos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.OAK));
        DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new BoatDispenseItemBehavior(Boat.Type.SPRUCE));
        DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new BoatDispenseItemBehavior(Boat.Type.BIRCH));
        DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new BoatDispenseItemBehavior(Boat.Type.JUNGLE));
        DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.DARK_OAK));
        DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new BoatDispenseItemBehavior(Boat.Type.ACACIA));
        DispenserBlock.registerBehavior(Items.CHERRY_BOAT, new BoatDispenseItemBehavior(Boat.Type.CHERRY));
        DispenserBlock.registerBehavior(Items.MANGROVE_BOAT, new BoatDispenseItemBehavior(Boat.Type.MANGROVE));
        DispenserBlock.registerBehavior(Items.BAMBOO_RAFT, new BoatDispenseItemBehavior(Boat.Type.BAMBOO));
        DispenserBlock.registerBehavior(Items.OAK_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.OAK, true));
        DispenserBlock.registerBehavior(Items.SPRUCE_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.SPRUCE, true));
        DispenserBlock.registerBehavior(Items.BIRCH_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.BIRCH, true));
        DispenserBlock.registerBehavior(Items.JUNGLE_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.JUNGLE, true));
        DispenserBlock.registerBehavior(Items.DARK_OAK_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.DARK_OAK, true));
        DispenserBlock.registerBehavior(Items.ACACIA_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.ACACIA, true));
        DispenserBlock.registerBehavior(Items.CHERRY_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.CHERRY, true));
        DispenserBlock.registerBehavior(Items.MANGROVE_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.MANGROVE, true));
        DispenserBlock.registerBehavior(Items.BAMBOO_CHEST_RAFT, new BoatDispenseItemBehavior(Boat.Type.BAMBOO, true));
        DispenseItemBehavior dispenseItemBehavior = new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem)itemStack.getItem();
                BlockPos blockPos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
                Level level = blockSource.level();
                // CraftBukkit start
                int x = blockPos.getX();
                int y = blockPos.getY();
                int z = blockPos.getZ();
                BlockState iblockdata = level.getBlockState(blockPos);
                if (iblockdata.isAir() || iblockdata.canBeReplaced() || (dispensibleContainerItem instanceof BucketItem && iblockdata.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer) iblockdata.getBlock()).canPlaceLiquid((Player) null, level, blockPos, iblockdata, ((BucketItem) dispensibleContainerItem).content))) {
                    org.bukkit.block.Block block = CraftBlock.at(level, blockSource.pos());
                    CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack);

                    BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(x, y, z));
                    if (!DispenserBlock.eventFired) {
                        level.getCraftServer().getPluginManager().callEvent(event);
                    }

                    if (event.isCancelled()) {
                        return itemStack;
                    }

                    if (!event.getItem().equals(craftItem)) {
                        // Chain to handler for new item
                        ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                        DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
                        if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != this) {
                            idispensebehavior.dispense(blockSource, eventStack);
                            return itemStack;
                        }
                    }

                    dispensibleContainerItem = (DispensibleContainerItem) CraftItemStack.asNMSCopy(event.getItem()).getItem();
                }
                // CraftBukkit end
                if (dispensibleContainerItem.emptyContents((Player)null, level, blockPos, (BlockHitResult)null)) {
                    dispensibleContainerItem.checkExtraContent((Player)null, level, itemStack, blockPos);
                    // CraftBukkit start - Handle stacked buckets
                    Item item = Items.BUCKET;
                    itemStack.shrink(1);
                    if (itemStack.isEmpty()) {
                        itemStack.setItem(Items.BUCKET);
                        itemStack.setCount(1);
                    } else if (blockSource.blockEntity().addItem(new ItemStack(item)) < 0) {
                        this.defaultDispenseItemBehavior.dispense(blockSource, new ItemStack(item));
                    }
                    return itemStack;
                    // CraftBukkit end
                } else {
                    return this.defaultDispenseItemBehavior.dispense(blockSource, itemStack);
                }
            }
        };
        DispenserBlock.registerBehavior(Items.LAVA_BUCKET, dispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.WATER_BUCKET, dispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.POWDER_SNOW_BUCKET, dispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.SALMON_BUCKET, dispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.COD_BUCKET, dispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, dispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, dispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.AXOLOTL_BUCKET, dispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.TADPOLE_BUCKET, dispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.BUCKET, new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Level levelAccessor = blockSource.level();
                BlockPos blockPos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
                BlockState blockState = levelAccessor.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (block instanceof BucketPickup bucketPickup) {
                    ItemStack itemStack2 = bucketPickup.pickupBlock((Player) null, DummyGeneratorAccess.INSTANCE, blockPos, blockState); // CraftBukkit
                    if (itemStack2.isEmpty()) {
                        return super.execute(blockSource, itemStack);
                    } else {
                        levelAccessor.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, blockPos);
                        Item item = itemStack2.getItem();
                        // CraftBukkit start
                        org.bukkit.block.Block bukkitBlock = CraftBlock.at(levelAccessor, blockSource.pos());
                        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack2);

                        BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                        if (!DispenserBlock.eventFired) {
                            levelAccessor.getCraftServer().getPluginManager().callEvent(event);
                        }

                        if (event.isCancelled()) {
                            return itemStack2;
                        }

                        if (!event.getItem().equals(craftItem)) {
                            // Chain to handler for new item
                            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                            DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
                            if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != this) {
                                idispensebehavior.dispense(blockSource, eventStack);
                                return itemStack2;
                            }
                        }

                        itemStack2 = bucketPickup.pickupBlock((Player) null, levelAccessor, blockPos, blockState); // From above
                        // CraftBukkit end
                        itemStack.shrink(1);
                        if (itemStack.isEmpty()) {
                            return new ItemStack(item);
                        } else {
                            if (blockSource.blockEntity().addItem(new ItemStack(item)) < 0) {
                                this.defaultDispenseItemBehavior.dispense(blockSource, new ItemStack(item));
                            }

                            return itemStack;
                        }
                    }
                } else {
                    return super.execute(blockSource, itemStack);
                }
            }
        });
        DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseItemBehavior() {
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Level level = blockSource.level();
                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = CraftBlock.at(level, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                if (!DispenserBlock.eventFired) {
                    level.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemStack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(blockSource, eventStack);
                        return itemStack;
                    }
                }
                // CraftBukkit end
                this.setSuccess(true);
                Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.pos().relative(direction);
                BlockState blockState = level.getBlockState(blockPos);
                if (BaseFireBlock.canBePlacedAt(level, blockPos, direction)) {
                    //level.setBlockAndUpdate(blockPos, BaseFireBlock.getState(level, blockPos));
                    //level.gameEvent((Entity)null, GameEvent.BLOCK_PLACE, blockPos);
                    // CraftBukkit start - Ignition by dispensing flint and steel
                    if (!org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory.callBlockIgniteEvent(level, blockPos, blockSource.pos()).isCancelled()) {
                        level.setBlockAndUpdate(blockPos, BaseFireBlock.getState(level, blockPos));
                        level.gameEvent((Entity) null, GameEvent.BLOCK_PLACE, blockPos);
                    }
                    // CraftBukkit end
                } else if (!CampfireBlock.canLight(blockState) && !CandleBlock.canLight(blockState) && !CandleCakeBlock.canLight(blockState)) {
                    if (blockState.getBlock() instanceof TntBlock && org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory.callTNTPrimeEvent(level, blockPos, org.bukkit.event.block.TNTPrimeEvent.PrimeCause.DISPENSER, null, blockSource.pos())) { // CraftBukkit - TNTPrimeEvent
                        TntBlock.explode(level, blockPos);
                        level.removeBlock(blockPos, false);
                    } else {
                        this.setSuccess(false);
                    }
                } else {
                    level.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(BlockStateProperties.LIT, true));
                    level.gameEvent((Entity)null, GameEvent.BLOCK_CHANGE, blockPos);
                }

                if (this.isSuccess() && itemStack.hurt(1, level.random, (ServerPlayer)null)) {
                    itemStack.setCount(0);
                }

                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(Items.BONE_MEAL, new OptionalDispenseItemBehavior() {
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                this.setSuccess(true);
                Level level = blockSource.level();
                BlockPos blockPos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
                // CraftBukkit start
                org.bukkit.block.Block block = CraftBlock.at(level, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                if (!DispenserBlock.eventFired) {
                    level.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemStack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(blockSource, eventStack);
                        return itemStack;
                    }
                }

                level.captureTreeGeneration = true;
                // CraftBukkit end
                if (!BoneMealItem.growCrop(itemStack, level, blockPos) && !BoneMealItem.growWaterPlant(itemStack, level, blockPos, (Direction)null)) {
                    this.setSuccess(false);
                } else if (!level.isClientSide) {
                    level.levelEvent(1505, blockPos, 0);
                }

                // CraftBukkit start
                level.captureTreeGeneration = false;
                if (level.capturedBlockStates.size() > 0) {
                    TreeType treeType = SaplingBlock.treeType;
                    SaplingBlock.treeType = null;
                    Location location = CraftLocation.toBukkit(blockPos, level.getWorld());
                    List<org.bukkit.block.BlockState> blocks = new java.util.ArrayList<>(level.capturedBlockStates.values());
                    level.capturedBlockStates.clear();
                    StructureGrowEvent structureEvent = null;
                    if (treeType != null) {
                        structureEvent = new StructureGrowEvent(location, treeType, false, null, blocks);
                        org.bukkit.Bukkit.getPluginManager().callEvent(structureEvent);
                    }

                    BlockFertilizeEvent fertilizeEvent = new BlockFertilizeEvent(location.getBlock(), null, blocks);
                    fertilizeEvent.setCancelled(structureEvent != null && structureEvent.isCancelled());
                    org.bukkit.Bukkit.getPluginManager().callEvent(fertilizeEvent);

                    if (!fertilizeEvent.isCancelled()) {
                        for (org.bukkit.block.BlockState blockstate : blocks) {
                            blockstate.update(true);
                        }
                    }
                }
                // CraftBukkit end

                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(Blocks.TNT, new DefaultDispenseItemBehavior() {
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Level level = blockSource.level();
                BlockPos blockPos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));

                // CraftBukkit start
                ItemStack itemstack1 = itemStack.split(1);
                org.bukkit.block.Block block = CraftBlock.at(level, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector((double) blockPos.getX() + 0.5D, (double) blockPos.getY(), (double) blockPos.getZ() + 0.5D));
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

                PrimedTnt primedTnt = new PrimedTnt(level, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), (LivingEntity) null);
                // CraftBukkit end

                level.addFreshEntity(primedTnt);
                level.playSound((Player)null, primedTnt.getX(), primedTnt.getY(), primedTnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent((Entity)null, GameEvent.ENTITY_PLACE, blockPos);
                // itemStack.shrink(1); // CraftBukkit - handled above
                return itemStack;
            }
        });
        DispenseItemBehavior dispenseItemBehavior2 = new OptionalDispenseItemBehavior() {
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                this.setSuccess(ArmorItem.dispenseArmor(blockSource, itemStack));
                return itemStack;
            }
        };
        DispenserBlock.registerBehavior(Items.CREEPER_HEAD, dispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, dispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.DRAGON_HEAD, dispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.SKELETON_SKULL, dispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.PIGLIN_HEAD, dispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.PLAYER_HEAD, dispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new OptionalDispenseItemBehavior() {
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Level level = blockSource.level();
                Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.pos().relative(direction);
                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = CraftBlock.at(level, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                if (!DispenserBlock.eventFired) {
                    level.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemStack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(blockSource, eventStack);
                        return itemStack;
                    }
                }
                // CraftBukkit end
                if (level.isEmptyBlock(blockPos) && WitherSkullBlock.canSpawnMob(level, blockPos, itemStack)) {
                    level.setBlock(blockPos, (BlockState)Blocks.WITHER_SKELETON_SKULL.defaultBlockState().setValue(SkullBlock.ROTATION, RotationSegment.convertToSegment(direction)), 3);
                    level.gameEvent((Entity)null, GameEvent.BLOCK_PLACE, blockPos);
                    BlockEntity blockEntity = level.getBlockEntity(blockPos);
                    if (blockEntity instanceof SkullBlockEntity) {
                        WitherSkullBlock.checkSpawn(level, blockPos, (SkullBlockEntity)blockEntity);
                    }

                    itemStack.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor(blockSource, itemStack));
                }

                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new OptionalDispenseItemBehavior() {
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Level level = blockSource.level();
                BlockPos blockPos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
                CarvedPumpkinBlock carvedPumpkinBlock = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;

                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = CraftBlock.at(level, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                if (!DispenserBlock.eventFired) {
                    level.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemStack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(blockSource, eventStack);
                        return itemStack;
                    }
                }
                // CraftBukkit end
                
                if (level.isEmptyBlock(blockPos) && carvedPumpkinBlock.canSpawnGolem(level, blockPos)) {
                    if (!level.isClientSide) {
                        level.setBlock(blockPos, carvedPumpkinBlock.defaultBlockState(), 3);
                        level.gameEvent((Entity)null, GameEvent.BLOCK_PLACE, blockPos);
                    }

                    itemStack.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor(blockSource, itemStack));
                }

                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new ShulkerBoxDispenseBehavior());
        DyeColor[] var4 = DyeColor.values();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            DyeColor dyeColor = var4[var6];
            DispenserBlock.registerBehavior(ShulkerBoxBlock.getBlockByColor(dyeColor).asItem(), new ShulkerBoxDispenseBehavior());
        }

        DispenserBlock.registerBehavior(Items.GLASS_BOTTLE.asItem(), new OptionalDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            private ItemStack takeLiquid(BlockSource blockSource, ItemStack itemStack, ItemStack itemStack2) {
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    blockSource.level().gameEvent((Entity)null, GameEvent.FLUID_PICKUP, blockSource.pos());
                    return itemStack2.copy();
                } else {
                    if (blockSource.blockEntity().addItem(itemStack2.copy()) < 0) {
                        this.defaultDispenseItemBehavior.dispense(blockSource, itemStack2.copy());
                    }

                    return itemStack;
                }
            }

            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                this.setSuccess(false);
                ServerLevel serverLevel = blockSource.level();
                BlockPos blockPos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
                BlockState blockState = serverLevel.getBlockState(blockPos);

                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = CraftBlock.at(serverLevel, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                if (!DispenserBlock.eventFired) {
                    serverLevel.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemStack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(blockSource, eventStack);
                        return itemStack;
                    }
                }
                // CraftBukkit end
                
                if (blockState.is(BlockTags.BEEHIVES, (blockStateBase) -> {
                    return blockStateBase.hasProperty(BeehiveBlock.HONEY_LEVEL) && blockStateBase.getBlock() instanceof BeehiveBlock;
                }) && (Integer)blockState.getValue(BeehiveBlock.HONEY_LEVEL) >= 5) {
                    ((BeehiveBlock)blockState.getBlock()).releaseBeesAndResetHoneyLevel(serverLevel, blockState, blockPos, (Player)null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                    this.setSuccess(true);
                    return this.takeLiquid(blockSource, itemStack, new ItemStack(Items.HONEY_BOTTLE));
                } else if (serverLevel.getFluidState(blockPos).is(FluidTags.WATER)) {
                    this.setSuccess(true);
                    return this.takeLiquid(blockSource, itemStack, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
                } else {
                    return super.execute(blockSource, itemStack);
                }
            }
        });
        DispenserBlock.registerBehavior(Items.GLOWSTONE, new OptionalDispenseItemBehavior() {
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.pos().relative(direction);
                Level level = blockSource.level();
                BlockState blockState = level.getBlockState(blockPos);
                this.setSuccess(true);
                if (blockState.is(Blocks.RESPAWN_ANCHOR)) {
                    if ((Integer)blockState.getValue(RespawnAnchorBlock.CHARGE) != 4) {
                        RespawnAnchorBlock.charge((Entity)null, level, blockPos, blockState);
                        itemStack.shrink(1);
                    } else {
                        this.setSuccess(false);
                    }

                    return itemStack;
                } else {
                    return super.execute(blockSource, itemStack);
                }
            }
        });
        DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenseItemBehavior());
        DispenserBlock.registerBehavior(Items.HONEYCOMB, new OptionalDispenseItemBehavior() {
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                BlockPos blockPos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
                Level level = blockSource.level();
                BlockState blockState = level.getBlockState(blockPos);
                Optional<BlockState> optional = HoneycombItem.getWaxed(blockState);
                if (optional.isPresent()) {
                    level.setBlockAndUpdate(blockPos, (BlockState)optional.get());
                    level.levelEvent(3003, blockPos, 0);
                    itemStack.shrink(1);
                    this.setSuccess(true);
                    return itemStack;
                } else {
                    return super.execute(blockSource, itemStack);
                }
            }
        });
        DispenserBlock.registerBehavior(Items.POTION, new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                if (PotionUtils.getPotion(itemStack) != Potions.WATER) {
                    return this.defaultDispenseItemBehavior.dispense(blockSource, itemStack);
                } else {
                    ServerLevel serverLevel = blockSource.level();
                    BlockPos blockPos = blockSource.pos();
                    BlockPos blockPos2 = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
                    if (!serverLevel.getBlockState(blockPos2).is(BlockTags.CONVERTABLE_TO_MUD)) {
                        return this.defaultDispenseItemBehavior.dispense(blockSource, itemStack);
                    } else {
                        if (!serverLevel.isClientSide) {
                            for(int i = 0; i < 5; ++i) {
                                serverLevel.sendParticles(ParticleTypes.SPLASH, (double)blockPos.getX() + serverLevel.random.nextDouble(), (double)(blockPos.getY() + 1), (double)blockPos.getZ() + serverLevel.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                            }
                        }

                        serverLevel.playSound((Player)null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        serverLevel.gameEvent((Entity)null, GameEvent.FLUID_PLACE, blockPos);
                        serverLevel.setBlockAndUpdate(blockPos2, Blocks.MUD.defaultBlockState());
                        return new ItemStack(Items.GLASS_BOTTLE);
                    }
                }
            }
        });
    }
}
