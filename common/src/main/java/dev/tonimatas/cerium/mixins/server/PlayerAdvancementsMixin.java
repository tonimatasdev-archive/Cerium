package dev.tonimatas.cerium.mixins.server;

import dev.tonimatas.cerium.bridge.advancements.AdvancementHolderBridge;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.Set;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final private Path playerSavePath;
    @Shadow protected abstract void startProgress(AdvancementHolder advancementHolder, AdvancementProgress advancementProgress);
    @Shadow @Final private Set<AdvancementHolder> progressChanged;
    @Shadow protected abstract void markForVisibilityUpdate(AdvancementHolder advancementHolder);

    @Shadow private ServerPlayer player;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private void applyFrom(ServerAdvancementManager serverAdvancementManager, PlayerAdvancements.Data data) {
        data.forEach((resourceLocation, advancementProgress) -> {
            AdvancementHolder advancementHolder = serverAdvancementManager.get(resourceLocation);
            if (advancementHolder == null) {
                if (!resourceLocation.getNamespace().equals("minecraft")) return; // CraftBukkit
                LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", resourceLocation, this.playerSavePath);
            } else {
                this.startProgress(advancementHolder, advancementProgress);
                this.progressChanged.add(advancementHolder);
                this.markForVisibilityUpdate(advancementHolder);
            }
        });
    }

    @Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementHolder;value()Lnet/minecraft/advancements/Advancement;", shift = At.Shift.BEFORE))
    private void cerium$award(AdvancementHolder advancementHolder, String string, CallbackInfoReturnable<Boolean> cir) {
        this.player.level().getCraftServer().getPluginManager().callEvent(new org.bukkit.event.player.PlayerAdvancementDoneEvent(this.player.getBukkitEntity(), ((AdvancementHolderBridge) (Object) advancementHolder).toBukkit())); // CraftBukkit
    }
}
