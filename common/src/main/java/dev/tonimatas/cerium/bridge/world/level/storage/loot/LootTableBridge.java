package dev.tonimatas.cerium.bridge.world.level.storage.loot;

import net.minecraft.world.Container;
import net.minecraft.world.level.storage.loot.LootParams;
import org.bukkit.craftbukkit.v1_20_R3.CraftLootTable;

public interface LootTableBridge {
    CraftLootTable cerium$getCraftLootTable();
    void cerium$setCraftLootTable(CraftLootTable value);
    void fillInventory(Container container, LootParams lootParams, long l, boolean plugin);
}
