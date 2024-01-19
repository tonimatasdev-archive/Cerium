package dev.tonimatas.cerium.mixins.world.item;

import com.mojang.serialization.Dynamic;
import dev.tonimatas.cerium.bridge.world.item.ItemStackBridge;
import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackBridge {
    @Mutable @Shadow @Final @Deprecated @Nullable private Item item;
    @Shadow private int count;
    @Shadow @Nullable private CompoundTag tag;
    @Shadow public abstract Item getItem();
    @Shadow public abstract void setDamageValue(int i);
    @Shadow public abstract int getDamageValue();
    @Shadow public abstract CompoundTag save(CompoundTag compoundTag);
    @Shadow public abstract void setTag(@Nullable CompoundTag compoundTag);
    @Shadow public abstract boolean isDamageableItem();
    @Shadow public abstract int getMaxDamage();

    @Shadow public abstract int getCount();

    @Shadow public abstract InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand);

    @Shadow public abstract void setCount(int i);

    @Shadow public abstract boolean hasAdventureModePlaceTagForBlock(Registry<Block> registry, BlockInWorld blockInWorld);

    @Shadow public abstract void shrink(int i);

    @Shadow public abstract ItemStack copy();

    @Override
    public void convertStack(int version) {
        if (0 < version && version < CraftMagicNumbers.INSTANCE.getDataVersion()) {
            CompoundTag savedStack = new CompoundTag();
            this.save(savedStack);
            savedStack = (CompoundTag) MinecraftServer.getServer().fixerUpper.update(References.ITEM_STACK, new Dynamic(NbtOps.INSTANCE, savedStack), version, CraftMagicNumbers.INSTANCE.getDataVersion()).getValue();
            this.load(savedStack);
        }
    }

    @Override
    public void load(CompoundTag compoundTag) {
        this.item = (Item) BuiltInRegistries.ITEM.get(new ResourceLocation(compoundTag.getString("id")));
        this.count = compoundTag.getByte("Count");
        if (compoundTag.contains("tag", 10)) {
            this.tag = compoundTag.getCompound("tag").copy();
            this.getItem().verifyTagAfterLoad(this.tag);
        }

        if (this.getItem().canBeDepleted()) {
            this.setDamageValue(this.getDamageValue());
        }
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockInWorld blockInWorld = new BlockInWorld(useOnContext.getLevel(), blockPos, false);
        if (player != null && !player.getAbilities().mayBuild && !this.hasAdventureModePlaceTagForBlock(useOnContext.getLevel().registryAccess().registryOrThrow(Registries.BLOCK), blockInWorld)) {
            return InteractionResult.PASS;
        } else {
            Item item = this.getItem();
            CompoundTag oldData = this.getTagClone();
            int oldCount = this.getCount();
            ServerLevel world = (ServerLevel) useOnContext.getLevel();

            if (!(item instanceof BucketItem || item instanceof SolidBucketItem)) { // if not bucket
                world.captureBlockStates = true;
                // special case bonemeal
                if (item == Items.BONE_MEAL) {
                    world.captureTreeGeneration = true;
                }
            }
            InteractionResult enuminteractionresult;
            try {
                enuminteractionresult = item.useOn(useOnContext);
            } finally {
                world.captureBlockStates = false;
            }
            CompoundTag newData = this.getTagClone();
            int newCount = this.getCount();
            this.setCount(oldCount);
            this.setTagClone(oldData);
            if (enuminteractionresult.consumesAction() && world.captureTreeGeneration && world.capturedBlockStates.size() > 0) {
                world.captureTreeGeneration = false;
                Location location = CraftLocation.toBukkit(blockPos, world.getWorld());
                TreeType treeType = SaplingBlock.treeType;
                SaplingBlock.treeType = null;
                List<CraftBlockState> blocks = new java.util.ArrayList<>(world.capturedBlockStates.values());
                world.capturedBlockStates.clear();
                StructureGrowEvent structureEvent = null;
                if (treeType != null) {
                    boolean isBonemeal = getItem() == Items.BONE_MEAL;
                    structureEvent = new StructureGrowEvent(location, treeType, isBonemeal, (Player) player.getBukkitEntity(), (List<BlockState>) (List<? extends BlockState>) blocks);
                    org.bukkit.Bukkit.getPluginManager().callEvent(structureEvent);
                }

                BlockFertilizeEvent fertilizeEvent = new BlockFertilizeEvent(CraftBlock.at(world, blockPos), (Player) player.getBukkitEntity(), (List< BlockState>) (List<? extends BlockState>) blocks);
                fertilizeEvent.setCancelled(structureEvent != null && structureEvent.isCancelled());
                org.bukkit.Bukkit.getPluginManager().callEvent(fertilizeEvent);

                if (!fertilizeEvent.isCancelled()) {
                    // Change the stack to its new contents if it hasn't been tampered with.
                    if (this.getCount() == oldCount && Objects.equals(this.tag, oldData)) {
                        this.setTag(newData);
                        this.setCount(newCount);
                    }
                    for (CraftBlockState blockstate : blocks) {
                        world.setBlock(blockstate.getPosition(),blockstate.getHandle(), blockstate.getFlag()); // SPIGOT-7248 - manual update to avoid physics where appropriate
                    }
                    player.awardStat(Stats.ITEM_USED.get(item)); // SPIGOT-7236 - award stat
                }

                CeriumValues.openSign = null; // SPIGOT-6758 - Reset on early return
                return enuminteractionresult;
            }
            world.captureTreeGeneration = false;

            if (player != null && enuminteractionresult.shouldAwardStats()) {
                InteractionHand enumhand = useOnContext.getHand();
                org.bukkit.event.block.BlockPlaceEvent placeEvent = null;
                List<BlockState> blocks = new java.util.ArrayList<>(world.capturedBlockStates.values());
                world.capturedBlockStates.clear();
                if (blocks.size() > 1) {
                    placeEvent = CraftEventFactory.callBlockMultiPlaceEvent(world, player, enumhand, blocks, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                } else if (blocks.size() == 1) {
                    placeEvent = CraftEventFactory.callBlockPlaceEvent(world, player, enumhand, blocks.get(0), blockPos.getX(), blockPos.getY(), blockPos.getZ());
                }

                if (placeEvent != null && (placeEvent.isCancelled() || !placeEvent.canBuild())) {
                    enuminteractionresult = InteractionResult.FAIL; // cancel placement
                    // PAIL: Remove this when MC-99075 fixed
                    placeEvent.getPlayer().updateInventory();
                    // revert back all captured blocks
                    world.preventPoiUpdated = true; // CraftBukkit - SPIGOT-5710
                    for (BlockState blockstate : blocks) {
                        blockstate.update(true, false);
                    }
                    world.preventPoiUpdated = false;

                    // Brute force all possible updates
                    BlockPos placedPos = ((CraftBlock) placeEvent.getBlock()).getPosition();
                    for (Direction dir : Direction.values()) {
                        ((ServerPlayer) player).connection.send(new ClientboundBlockUpdatePacket(world, placedPos.relative(dir)));
                    }
                    CeriumValues.openSign = null; // SPIGOT-6758 - Reset on early return
                } else {
                    // Change the stack to its new contents if it hasn't been tampered with.
                    if (this.getCount() == oldCount && Objects.equals(this.tag, oldData)) {
                        this.setTag(newData);
                        this.setCount(newCount);
                    }

                    for (Map.Entry<BlockPos, BlockEntity> e : world.capturedTileEntities.entrySet()) {
                        world.setBlockEntity(e.getValue());
                    }

                    for (BlockState blockstate : blocks) {
                        int updateFlag = ((CraftBlockState) blockstate).getFlag();
                        net.minecraft.world.level.block.state.BlockState oldBlock = ((CraftBlockState) blockstate).getHandle();
                        BlockPos newblockposition = ((CraftBlockState) blockstate).getPosition();
                        net.minecraft.world.level.block.state.BlockState block = world.getBlockState(newblockposition);

                        if (!(block.getBlock() instanceof BaseEntityBlock)) { // Containers get placed automatically
                            block.getBlock().onPlace(block, world, newblockposition, oldBlock, true);
                        }

                        world.notifyAndUpdatePhysics(newblockposition, null, oldBlock, block, world.getBlockState(newblockposition), updateFlag, 512); // send null chunk as chunk.k() returns false by this point
                    }

                    // Special case juke boxes as they update their tile entity. Copied from ItemRecord.
                    // PAIL: checkme on updates.
                    if (this.item instanceof RecordItem) {
                        BlockEntity tileentity = world.getBlockEntity(blockPos);

                        if (tileentity instanceof JukeboxBlockEntity) {
                            JukeboxBlockEntity tileentityjukebox = (JukeboxBlockEntity) tileentity;

                            // There can only be one
                            ItemStack record = this.copy();
                            if (!record.isEmpty()) {
                                record.setCount(1);
                            }

                            tileentityjukebox.setTheItem(record);
                            world.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, world.getBlockState(blockPos)));
                        }

                        this.shrink(1);
                        player.awardStat(Stats.PLAY_RECORD);
                    }

                    if (this.item == Items.WITHER_SKELETON_SKULL) { // Special case skulls to allow wither spawns to be cancelled
                        BlockPos bp = blockPos;
                        if (!world.getBlockState(blockPos).canBeReplaced()) {
                            if (!world.getBlockState(blockPos).isSolid()) {
                                bp = null;
                            } else {
                                bp = bp.relative(useOnContext.getClickedFace());
                            }
                        }
                        if (bp != null) {
                            BlockEntity te = world.getBlockEntity(bp);
                            if (te instanceof SkullBlockEntity) {
                                WitherSkullBlock.checkSpawn(world, bp, (SkullBlockEntity) te);
                            }
                        }
                    }

                    // SPIGOT-4678
                    if (this.item instanceof SignItem && CeriumValues.openSign != null) {
                        try {
                            if (world.getBlockEntity(CeriumValues.openSign) instanceof SignBlockEntity tileentitysign) {
                                if (world.getBlockState(CeriumValues.openSign).getBlock() instanceof SignBlock blocksign) {
                                    blocksign.openTextEdit(player, tileentitysign, true, org.bukkit.event.player.PlayerSignOpenEvent.Cause.PLACE); // Craftbukkit
                                }
                            }
                        } finally {
                            CeriumValues.openSign = null;
                        }
                    }

                    // SPIGOT-7315: Moved from BlockBed#setPlacedBy
                    if (placeEvent != null && this.item instanceof BedItem) {
                        BlockPos position = ((CraftBlock) placeEvent.getBlock()).getPosition();
                        net.minecraft.world.level.block.state.BlockState blockData =  world.getBlockState(position);

                        if (blockData.getBlock() instanceof BedBlock) {
                            world.blockUpdated(position, Blocks.AIR);
                            blockData.updateNeighbourShapes(world, position, 3);
                        }
                    }

                    // SPIGOT-1288 - play sound stripped from ItemBlock
                    if (this.item instanceof BlockItem) {
                        SoundType soundeffecttype = ((BlockItem) this.item).getBlock().defaultBlockState().getSoundType();
                        world.playSound(player, blockPos, soundeffecttype.getPlaceSound(), SoundSource.BLOCKS, (soundeffecttype.getVolume() + 1.0F) / 2.0F, soundeffecttype.getPitch() * 0.8F);
                    }

                    player.awardStat(Stats.ITEM_USED.get(item));
                }
            }

            world.capturedTileEntities.clear();
            world.capturedBlockStates.clear();

            return enuminteractionresult;
        }
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public boolean hurt(int i, RandomSource randomSource, @Nullable ServerPlayer serverPlayer) {
        if (!this.isDamageableItem()) {
            return false;
        } else {
            int j;
            if (i > 0) {
                j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, (ItemStack) (Object) this);
                int k = 0;

                for(int l = 0; j > 0 && l < i; ++l) {
                    if (DigDurabilityEnchantment.shouldIgnoreDurabilityDrop((ItemStack) (Object) this, j, randomSource)) {
                        ++k;
                    }
                }

                i -= k;
                if (serverPlayer != null) {
                    PlayerItemDamageEvent event = new PlayerItemDamageEvent(serverPlayer.getBukkitEntity(), CraftItemStack.asCraftMirror((ItemStack) (Object) this), i);
                    event.getPlayer().getServer().getPluginManager().callEvent(event);

                    if (i != event.getDamage() || event.isCancelled()) {
                        event.getPlayer().updateInventory();
                    }
                    if (event.isCancelled()) {
                        return false;
                    }

                    i = event.getDamage();
                }
                if (i <= 0) {
                    return false;
                }
            }

            if (serverPlayer != null && i != 0) {
                CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(serverPlayer, (ItemStack) (Object) this, this.getDamageValue() + i);
            }

            j = this.getDamageValue() + i;
            this.setDamageValue(j);
            return j >= this.getMaxDamage();
        }
    }

    @Inject(method = "hurtAndBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", shift = At.Shift.AFTER))
    private <T extends LivingEntity> void cerium$hurtAndBreak(int i, T livingEntity, Consumer<T> consumer, CallbackInfo ci) {
        if (this.count == 1 && livingEntity instanceof Player) {
            org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory.callPlayerItemBreakEvent((Player) livingEntity, (ItemStack) (Object) this);
        }
    }

    @Override
    public CompoundTag getTagClone() {
        return this.tag == null ? null : this.tag.copy();
    }

    @Override
    public void setTagClone(@Nullable CompoundTag compoundTag) {
        this.setTag(compoundTag == null ? null : compoundTag.copy());
    }

    @Override
    public void setItem(Item item) {
        this.item = item;
    }
}
