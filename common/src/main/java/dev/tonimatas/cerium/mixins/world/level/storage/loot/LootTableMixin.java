package dev.tonimatas.cerium.mixins.world.level.storage.loot;

import dev.tonimatas.cerium.bridge.world.level.storage.loot.LootTableBridge;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.craftbukkit.v1_20_R3.CraftLootTable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.Optional;

@Mixin(LootTable.class)
public abstract class LootTableMixin implements LootTableBridge {
    @Shadow protected abstract ObjectArrayList<ItemStack> getRandomItems(LootContext lootContext);
    @Shadow @Final private Optional<ResourceLocation> randomSequence;
    @Shadow protected abstract List<Integer> getAvailableSlots(Container container, RandomSource randomSource);
    @Shadow protected abstract void shuffleAndSplitItems(ObjectArrayList<ItemStack> objectArrayList, int i, RandomSource randomSource);
    @Shadow @Final private static Logger LOGGER;
    @Unique public CraftLootTable craftLootTable; // CraftBukkit

    @Override
    public CraftLootTable cerium$getCraftLootTable() {
        return craftLootTable;
    }

    @Override
    public void cerium$setCraftLootTable(CraftLootTable value) {
        this.craftLootTable = value;
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void fill(Container container, LootParams lootParams, long l) {
        this.fillInventory(container, lootParams, l, false);
    }
    
    @Override
    public void fillInventory(Container container, LootParams lootParams, long l, boolean plugin) {
        LootContext lootContext = (new LootContext.Builder(lootParams)).withOptionalRandomSeed(l).create(this.randomSequence);
        ObjectArrayList<ItemStack> objectArrayList = this.getRandomItems(lootContext);
        RandomSource randomSource = lootContext.getRandom();
        List<Integer> list = this.getAvailableSlots(container, randomSource);
        this.shuffleAndSplitItems(objectArrayList, list.size(), randomSource);

        for (ItemStack itemStack : objectArrayList) {
            if (list.isEmpty()) {
                LOGGER.warn("Tried to over-fill a container");
                return;
            }

            if (itemStack.isEmpty()) {
                container.setItem((Integer) list.remove(list.size() - 1), ItemStack.EMPTY);
            } else {
                container.setItem((Integer) list.remove(list.size() - 1), itemStack);
            }
        }

    }
}
