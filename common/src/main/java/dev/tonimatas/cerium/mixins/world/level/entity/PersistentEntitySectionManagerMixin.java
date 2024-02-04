package dev.tonimatas.cerium.mixins.world.level.entity;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.tonimatas.cerium.bridge.world.level.entity.PersistentEntitySectionManagerBridge;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.entity.*;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Mixin(PersistentEntitySectionManager.class)
public abstract class PersistentEntitySectionManagerMixin<T extends EntityAccess> implements PersistentEntitySectionManagerBridge {
    @Shadow @Final private EntitySectionStorage<?> sectionStorage;

    @Shadow @Final private Long2ObjectMap<PersistentEntitySectionManager.ChunkLoadStatus> chunkLoadStatuses;

    @Shadow @Final public EntityPersistentStorage<T> permanentStorage;

    @Shadow protected abstract void requestChunkLoad(long l);

    @Shadow public abstract void close() throws IOException;

    // CraftBukkit start - add method to get all entities in chunk
    public List<Entity> getEntities(ChunkPos chunkCoordIntPair) {
        return sectionStorage.getExistingSectionsInChunk(chunkCoordIntPair.toLong()).flatMap(EntitySection::getEntities).map(entity -> (Entity) entity).collect(Collectors.toList());
    }

    public boolean isPending(long pair) {
        return chunkLoadStatuses.get(pair) == PersistentEntitySectionManager.ChunkLoadStatus.PENDING;
    }
    // CraftBukkit end

    @Unique private AtomicBoolean cerium$callEvent = new AtomicBoolean(false);

    private boolean storeChunkSections(long l, Consumer<T> consumer, boolean callEvent) {
        cerium$callEvent.set(callEvent);
        return storeChunkSections(l, consumer);
    }
    
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private boolean storeChunkSections(long l, Consumer<T> consumer) {
        boolean callEvent = cerium$callEvent.getAndSet(false);
        PersistentEntitySectionManager.ChunkLoadStatus chunkLoadStatus = (PersistentEntitySectionManager.ChunkLoadStatus)this.chunkLoadStatuses.get(l);
        if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.PENDING) {
            return false;
        } else {
            List<T> list = (List)this.sectionStorage.getExistingSectionsInChunk(l).flatMap((entitySection) -> {
                return entitySection.getEntities().filter(EntityAccess::shouldBeSaved);
            }).collect(Collectors.toList());
            if (list.isEmpty()) {
                if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.LOADED) {
                    if (callEvent) CraftEventFactory.callEntitiesUnloadEvent(((EntityStorage) permanentStorage).level, new ChunkPos(l), ImmutableList.of()); // CraftBukkit
                    this.permanentStorage.storeEntities(new ChunkEntities(new ChunkPos(l), ImmutableList.of()));
                }

                return true;
            } else if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.FRESH) {
                this.requestChunkLoad(l);
                return false;
            } else {
                if (callEvent) CraftEventFactory.callEntitiesUnloadEvent(((EntityStorage) permanentStorage).level, new ChunkPos(l), list.stream().map(entity -> (Entity) entity).collect(Collectors.toList())); // CraftBukkit
                this.permanentStorage.storeEntities(new ChunkEntities(new ChunkPos(l), list));
                list.forEach(consumer);
                return true;
            }
        }
    }
    
    @Inject(method = "processPendingLoads", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;put(JLjava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void cerium$processPendingLoads(CallbackInfo ci, ChunkEntities chunkEntities) {
        // CraftBukkit start - call entity load event
        List<Entity> entities = getEntities(chunkEntities.getPos());
        CraftEventFactory.callEntitiesLoadEvent(((EntityStorage) permanentStorage).level, chunkEntities.getPos(), entities);
        // CraftBukkit end
    }

    @Unique private AtomicBoolean cerium$save = new AtomicBoolean(true);
    
    @WrapWithCondition(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;saveAll()V"))
    private boolean cerium$close(PersistentEntitySectionManager instance) {
        return cerium$save.getAndSet(true);
    }

    public void close(boolean save) throws IOException {
        cerium$save.set(save);
        this.close();
    }
}
