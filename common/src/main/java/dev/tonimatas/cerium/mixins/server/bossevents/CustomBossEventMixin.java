package dev.tonimatas.cerium.mixins.server.bossevents;

import dev.tonimatas.cerium.bridge.server.bossevents.CustomBossEventBridge;
import net.minecraft.server.bossevents.CustomBossEvent;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.craftbukkit.v1_20_R3.boss.CraftKeyedBossbar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CustomBossEvent.class)
public class CustomBossEventMixin implements CustomBossEventBridge {
    @Unique private KeyedBossBar bossBar;

    @Override
    public KeyedBossBar getBukkitEntity() {
        if (bossBar == null) {
            bossBar = new CraftKeyedBossbar((CustomBossEvent) (Object) this);
        }
        return bossBar;
    }
}
