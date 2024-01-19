package dev.tonimatas.cerium.mixins.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.tonimatas.cerium.bridge.world.level.storage.loot.LootTableBridge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.bukkit.craftbukkit.v1_20_R3.CraftLootTable;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(LootDataManager.class)
public abstract class LootDataManagerMixin {
    @Shadow @Final private static Logger LOGGER;

    @Shadow private Map<LootDataId<?>, ?> elements;

    @Shadow private Multimap<LootDataType<?>, ResourceLocation> typeKeys;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private void apply(Map<LootDataType<?>, Map<ResourceLocation, ?>> map) {
        Object object = ((Map)map.get(LootDataType.TABLE)).remove(BuiltInLootTables.EMPTY);
        if (object != null) {
            LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", BuiltInLootTables.EMPTY);
        }

        ImmutableMap.Builder<LootDataId<?>, Object> builder = ImmutableMap.builder();
        ImmutableMultimap.Builder<LootDataType<?>, ResourceLocation> builder2 = ImmutableMultimap.builder();
        map.forEach((lootDataType, mapx) -> {
            mapx.forEach((resourceLocation, object1) -> {
                builder.put(new LootDataId(lootDataType, resourceLocation), object1);
                builder2.put(lootDataType, resourceLocation);
            });
        });
        builder.put(LootDataManager.EMPTY_LOOT_TABLE_KEY, LootTable.EMPTY);
        ProblemReporter.Collector collector = new ProblemReporter.Collector();
        final Map<LootDataId<?>, ?> map2 = builder.build();
        ValidationContext validationContext = new ValidationContext(collector, LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
            @Nullable
            public <T> T getElement(LootDataId<T> lootDataId) {
                return (T) map2.get(lootDataId);
            }
        });
        map2.forEach((lootDataId, objectx) -> {
            LootDataManager.castAndValidate(validationContext, lootDataId, objectx);
        });
        collector.get().forEach((string, string2) -> {
            LOGGER.warn("Found loot table element validation problem in {}: {}", string, string2);
        });
        // CraftBukkit start
        map2.forEach((key, lootTable) -> {
            if (object instanceof LootTable table) {
                ((LootTableBridge) table).cerium$setCraftLootTable(new CraftLootTable(CraftNamespacedKey.fromMinecraft(key.location()), table));
            }
        });
        // CraftBukkit end
        this.elements = map2;
        this.typeKeys = builder2.build();
    }
}
