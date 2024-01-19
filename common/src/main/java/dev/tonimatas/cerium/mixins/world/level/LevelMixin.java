package dev.tonimatas.cerium.mixins.world.level;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tonimatas.cerium.bridge.world.level.LevelBridge;
import dev.tonimatas.cerium.util.CeriumClasses;
import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.WritableLevelData;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CapturedBlockState;
import org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftSpawnCategory;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelBridge {
    @Shadow public abstract boolean isDebug();

    @Shadow @Final public boolean isClientSide;
    @Shadow public abstract LevelChunk getChunkAt(BlockPos blockPos);
    @Shadow public abstract BlockState getBlockState(BlockPos blockPos);
    @Shadow public abstract void setBlocksDirty(BlockPos blockPos, BlockState blockState, BlockState blockState2);
    @Shadow public abstract void sendBlockUpdated(BlockPos blockPos, BlockState blockState, BlockState blockState2, int i);
    @Shadow public abstract void updateNeighbourForOutputSignal(BlockPos blockPos, Block block);
    @Shadow public abstract void onBlockStateChange(BlockPos blockPos, BlockState blockState, BlockState blockState2);

    @Shadow @Nullable public abstract BlockEntity getBlockEntity(BlockPos blockPos);

    @Shadow public abstract DimensionType dimensionType();

    @Mutable
    @Shadow @Final private WorldBorder worldBorder;
    @Unique private CraftWorld world;
    @Unique public boolean pvpMode;
    @Unique public boolean keepSpawnInMemory = true;
    @Unique public org.bukkit.generator.ChunkGenerator generator;

    @Unique public boolean preventPoiUpdated = false; // CraftBukkit - SPIGOT-5710
    @Unique public boolean captureBlockStates = false;
    @Unique public boolean captureTreeGeneration = false;
    @Unique public Map<BlockPos, CapturedBlockState> capturedBlockStates = new java.util.LinkedHashMap<>();
    @Unique public Map<BlockPos, BlockEntity> capturedTileEntities = new HashMap<>();
    @Unique public List<ItemEntity> captureDrops;
    @Unique public final it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap<SpawnCategory> ticksPerSpawnCategory = new it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap<>();
    @Unique public boolean populating;

    @Override
    public CraftWorld getWorld() {
        return this.world;
    }

    @Override
    public CraftServer getCraftServer() {
        return (CraftServer) Bukkit.getServer();
    }

    @Override
    public abstract ResourceKey<LevelStem> getTypeKey();
    
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void cerium$init(WritableLevelData writableLevelData, ResourceKey resourceKey, RegistryAccess registryAccess, Holder holder, Supplier supplier, boolean bl, boolean bl2, long l, int i, CallbackInfo ci) {
        CeriumClasses.WorldInfo worldInfo = CeriumValues.worldInfo;
        this.generator = worldInfo.gen();
        this.world = new CraftWorld((ServerLevel) (Object) this, worldInfo.gen(), worldInfo.biomeProvider(), worldInfo.env());

        // CraftBukkit Ticks things
        for (SpawnCategory spawnCategory : SpawnCategory.values()) {
            if (CraftSpawnCategory.isValidForLimits(spawnCategory)) {
                this.ticksPerSpawnCategory.put(spawnCategory, (long) this.getCraftServer().getTicksPerSpawns(spawnCategory));
            }
        }

        // CraftBukkit end
    }
    
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void cerium$init$return(WritableLevelData writableLevelData, ResourceKey resourceKey, RegistryAccess registryAccess, Holder holder, Supplier supplier, boolean bl, boolean bl2, long l, int i, CallbackInfo ci) {
        // CraftBukkit start
        getWorldBorder().world = (ServerLevel) (Object) this;
        // From PlayerList.setPlayerFileData
        getWorldBorder().addListener(new BorderChangeListener() {
            @Override
            public void onBorderSizeSet(WorldBorder worldborder, double d0) {
                getCraftServer().getHandle().broadcastAll(new ClientboundSetBorderSizePacket(worldborder), worldborder.world);
            }

            @Override
            public void onBorderSizeLerping(WorldBorder worldborder, double d0, double d1, long i) {
                getCraftServer().getHandle().broadcastAll(new ClientboundSetBorderLerpSizePacket(worldborder), worldborder.world);
            }

            @Override
            public void onBorderCenterSet(WorldBorder worldborder, double d0, double d1) {
                getCraftServer().getHandle().broadcastAll(new ClientboundSetBorderCenterPacket(worldborder), worldborder.world);
            }

            @Override
            public void onBorderSetWarningTime(WorldBorder worldborder, int i) {
                getCraftServer().getHandle().broadcastAll(new ClientboundSetBorderWarningDelayPacket(worldborder), worldborder.world);
            }

            @Override
            public void onBorderSetWarningBlocks(WorldBorder worldborder, int i) {
                getCraftServer().getHandle().broadcastAll(new ClientboundSetBorderWarningDistancePacket(worldborder), worldborder.world);
            }

            @Override
            public void onBorderSetDamagePerBlock(WorldBorder worldborder, double d0) {}

            @Override
            public void onBorderSetDamageSafeZOne(WorldBorder worldborder, double d0) {}
        });

        if (dimensionType().coordinateScale() != 1.0) {
            this.worldBorder = new WorldBorder() {
                public double getCenterX() {
                    return super.getCenterX();
                }

                public double getCenterZ() {
                    return super.getCenterZ();
                }
            };
        } else {
            this.worldBorder = new WorldBorder();
        }
        // CraftBukkit end
    }
    
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public boolean setBlock(BlockPos blockPos, BlockState blockState, int i, int j) {
        // CraftBukkit start - tree generation
        if (this.captureTreeGeneration) {
            CapturedBlockState blockstate = capturedBlockStates.get(blockPos);
            if (blockstate == null) {
                blockstate = CapturedBlockState.getTreeBlockState((Level) (Object) this, blockPos, i);
                this.capturedBlockStates.put(blockPos.immutable(), blockstate);
            }
            blockstate.setData(blockState);
            return true;
        }
        // CraftBukkit end
        if (((Level) (Object) this).isOutsideBuildHeight(blockPos)) {
            return false;
        } else if (!this.isClientSide && this.isDebug()) {
            return false;
        } else {
            LevelChunk levelChunk = this.getChunkAt(blockPos);
            Block block = blockState.getBlock();
            // CraftBukkit start - capture blockstates
            boolean captured = false;
            if (this.captureBlockStates && !this.capturedBlockStates.containsKey(blockPos)) {
                CapturedBlockState blockstate = CapturedBlockState.getBlockState((Level) (Object) this, blockPos, i);
                this.capturedBlockStates.put(blockPos.immutable(), blockstate);
                captured = true;
            }
            // CraftBukkit end

            BlockState blockState2 = levelChunk.setBlockState(blockPos, blockState, (i & 64) != 0, (i & 1024) == 0); // CraftBukkit custom NO_PLACE flag
            if (blockState2 == null) {
                // CraftBukkit start - remove blockstate if failed (or the same)
                if (this.captureBlockStates && captured) {
                    this.capturedBlockStates.remove(blockPos);
                }
                // CraftBukkit end
                return false;
            } else {
                BlockState blockState3 = this.getBlockState(blockPos);
                /*
                if (blockState3 == blockState) {
                    if (blockState2 != blockState3) {
                        this.setBlocksDirty(blockPos, blockState2, blockState3);
                    }

                    if ((i & 2) != 0 && (!this.isClientSide || (i & 4) == 0) && (this.isClientSide || levelChunk.getFullStatus() != null && levelChunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING))) {
                        this.sendBlockUpdated(blockPos, blockState2, blockState, i);
                    }

                    if ((i & 1) != 0) {
                        ((Level) (Object) this).blockUpdated(blockPos, blockState2.getBlock());
                        if (!this.isClientSide && blockState.hasAnalogOutputSignal()) {
                            this.updateNeighbourForOutputSignal(blockPos, block);
                        }
                    }

                    if ((i & 16) == 0 && j > 0) {
                        int k = i & -34;
                        blockState2.updateIndirectNeighbourShapes((Level) (Object) this, blockPos, k, j - 1);
                        blockState.updateNeighbourShapes((Level) (Object) this, blockPos, k, j - 1);
                        blockState.updateIndirectNeighbourShapes((Level) (Object) this, blockPos, k, j - 1);
                    }

                    this.onBlockStateChange(blockPos, blockState2, blockState3);
                }
                */

                // CraftBukkit start
                if (!this.captureBlockStates) { // Don't notify clients or update physics while capturing blockstates
                    // Modularize client and physic updates
                    notifyAndUpdatePhysics(blockPos, levelChunk, blockState2, blockState, blockState3, i, j);
                }
                // CraftBukkit end

                return true;
            }
        }
    }

    // CraftBukkit start - Split off from above in order to directly send client and physic updates
    public void notifyAndUpdatePhysics(BlockPos blockposition, LevelChunk chunk, BlockState oldBlock, BlockState newBlock, BlockState actualBlock, int i, int j) {
        BlockState iblockdata = newBlock;
        BlockState iblockdata1 = oldBlock;
        BlockState iblockdata2 = actualBlock;
        if (iblockdata2 == iblockdata) {
            if (iblockdata1 != iblockdata2) {
                this.setBlocksDirty(blockposition, iblockdata1, iblockdata2);
            }

            if ((i & 2) != 0 && (!this.isClientSide || (i & 4) == 0) && (this.isClientSide || chunk == null || (chunk.getFullStatus() != null && chunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING)))) { // allow chunk to be null here as chunk.isReady() is false when we send our notification during block placement
                this.sendBlockUpdated(blockposition, iblockdata1, iblockdata, i);
            }

            if ((i & 1) != 0) {
                ((Level) (Object) this).blockUpdated(blockposition, iblockdata1.getBlock());
                if (!this.isClientSide && iblockdata.hasAnalogOutputSignal()) {
                    this.updateNeighbourForOutputSignal(blockposition, newBlock.getBlock());
                }
            }

            if ((i & 16) == 0 && j > 0) {
                int k = i & -34;

                // CraftBukkit start
                iblockdata1.updateIndirectNeighbourShapes((Level) (Object) this, blockposition, k, j - 1); // Don't call an event for the old block to limit event spam
                CraftWorld world = ((ServerLevel) (Object) this).getWorld();
                if (world != null) {
                    BlockPhysicsEvent event = new BlockPhysicsEvent(world.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), CraftBlockData.fromData(iblockdata));
                    this.getCraftServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return;
                    }
                }
                // CraftBukkit end
                iblockdata.updateNeighbourShapes((Level) (Object) this, blockposition, k, j - 1);
                iblockdata.updateIndirectNeighbourShapes((Level) (Object) this, blockposition, k, j - 1);
            }

            // CraftBukkit start - SPIGOT-5710
            if (!preventPoiUpdated) {
                this.onBlockStateChange(blockposition, iblockdata1, iblockdata2);
            }
            // CraftBukkit end
        }
    }
    // CraftBukkit end
    
    @Inject(method = "getBlockState", at = @At(value = "HEAD"))
    private void cerium$getBlockState(BlockPos blockPos, CallbackInfoReturnable<BlockState> cir) {
        // CraftBukkit start - tree generation
        if (captureTreeGeneration) {
            CapturedBlockState previous = capturedBlockStates.get(blockPos);
            if (previous != null) {
                cir.setReturnValue(previous.getHandle());
            }
        }
        // CraftBukkit end
    }
    
    @Inject(method = "getBlockEntity", at = @At(value = "HEAD"))
    private void cerium$getBlockEntity(BlockPos blockPos, CallbackInfoReturnable<BlockEntity> cir) {
        if (capturedTileEntities.containsKey(blockPos)) {
            cir.setReturnValue(capturedTileEntities.get(blockPos));
        }
    }

    @Unique
    @Nullable
    public BlockEntity getBlockEntity(BlockPos blockPos, boolean validate) {
        return this.getBlockEntity(blockPos);
    }
    
    @Inject(method = "setBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getChunkAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/chunk/LevelChunk;", shift = At.Shift.BEFORE))
    private void cerium$setBlockEntity(BlockEntity blockEntity, CallbackInfo ci, @Local BlockPos blockPos) {
        // CraftBukkit start
        if (captureBlockStates) {
            capturedTileEntities.put(blockPos.immutable(), blockEntity);
            return;
        }
        // CraftBukkit end
    }
}
