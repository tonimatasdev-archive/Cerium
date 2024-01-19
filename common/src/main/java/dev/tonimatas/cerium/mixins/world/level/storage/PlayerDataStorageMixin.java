package dev.tonimatas.cerium.mixins.world.level.storage;

import dev.tonimatas.cerium.bridge.world.level.storage.PlayerDataStorageBridge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(PlayerDataStorage.class)
public class PlayerDataStorageMixin implements PlayerDataStorageBridge {
    @Shadow @Final private File playerDir;
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtUtils;getDataVersion(Lnet/minecraft/nbt/CompoundTag;I)I"))
    private void cerium$load(Player entityhuman, CallbackInfoReturnable<CompoundTag> cir) {
        // CraftBukkit start
        if (entityhuman instanceof ServerPlayer) {
            CraftPlayer player = (CraftPlayer) entityhuman.getBukkitEntity();
            // Only update first played if it is older than the one we have
            long modified = new File(this.playerDir, entityhuman.getUUID().toString() + ".dat").lastModified();
            if (modified < player.getFirstPlayed()) {
                player.setFirstPlayed(modified);
            }
        }
        // CraftBukkit end
    }

    // CraftBukkit start
    @Override
    public CompoundTag getPlayerData(String s) {
        try {
            File file1 = new File(this.playerDir, s + ".dat");

            if (file1.exists()) {
                return NbtIo.readCompressed(file1.toPath(), NbtAccounter.unlimitedHeap());
            }
        } catch (Exception exception) {
            LOGGER.warn("Failed to load player data for " + s);
        }

        return null;
    }
    // CraftBukkit end

    // CraftBukkit start
    @Override
    public File getPlayerDir() {
        return playerDir;
    }
    // CraftBukkit end
}
