package dev.tonimatas.cerium.mixins.world.level.storage;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.tonimatas.cerium.bridge.world.level.storage.PrimaryLevelDataBridge;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.bukkit.Bukkit;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin implements PrimaryLevelDataBridge {
    @Shadow private LevelSettings settings;
    @Shadow public abstract Difficulty getDifficulty();
    @Shadow public abstract boolean isDifficultyLocked();

    @Shadow private boolean raining;

    @Shadow public abstract String getLevelName();

    @Shadow private boolean thundering;
    @Shadow @Final private WorldOptions worldOptions;
    // CraftBukkit start - Add world and pdc
    @Unique public Registry<LevelStem> customDimensions;

    @Override
    public Registry<LevelStem> cerium$getCustomDimension() {
        return customDimensions;
    }

    @Override
    public void cerium$setCustomDimension(Registry<LevelStem> value) {
        this.customDimensions = value;
    }

    @Unique private ServerLevel world;
    @Unique protected Tag pdc;

    @Override
    public Tag cerium$getPDC() {
        return pdc;
    }

    @Override
    public void cerium$setPDC(Tag value) {
        this.pdc = value;
    }

    public void setWorld(ServerLevel world) {
        if (this.world != null) {
            return;
        }
        this.world = world;
        world.getWorld().readBukkitValues(pdc);
        pdc = null;
    }
    // CraftBukkit end

    @Redirect(method = "setTagData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/WorldGenSettings;encode(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/world/level/levelgen/WorldOptions;Lnet/minecraft/core/RegistryAccess;)Lcom/mojang/serialization/DataResult;"))
    private <T> DataResult<T> cerium$setTagData(DynamicOps<T> dynamicOps, WorldOptions arg, RegistryAccess arg2) {
        return WorldGenSettings.encode(dynamicOps, this.worldOptions, new WorldDimensions(this.customDimensions != null ? this.customDimensions : arg2.registryOrThrow(Registries.LEVEL_STEM))); // CraftBukkit
    }
    
    @Inject(method = "setTagData", at = @At(value = "RETURN"))
    private void cerium$setTagData$return(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag2, CallbackInfo ci) {
        compoundTag.putString("Bukkit.Version", Bukkit.getName() + "/" + Bukkit.getVersion() + "/" + Bukkit.getBukkitVersion()); // CraftBukkit
        world.getWorld().storeBukkitValues(compoundTag); // CraftBukkit - add pdc
    }
    
    @Inject(method = "setThundering", at = @At(value = "HEAD"), cancellable = true)
    private void cerium$setThundering(boolean bl, CallbackInfo ci) {
        // CraftBukkit start
        if (this.thundering == bl) {
            ci.cancel();
        }

        org.bukkit.World world = Bukkit.getWorld(getLevelName());
        if (world != null) {
            ThunderChangeEvent thunder = new ThunderChangeEvent(world, bl);
            Bukkit.getServer().getPluginManager().callEvent(thunder);
            if (thunder.isCancelled()) {
                ci.cancel();
            }
        }
        // CraftBukkit end
    }
    
    @Inject(method = "setRaining", at = @At(value = "HEAD"), cancellable = true)
    private void cerium$setRaining(boolean bl, CallbackInfo ci) {
        // CraftBukkit start
        if (this.raining == bl) {
            ci.cancel();
        }

        org.bukkit.World world = Bukkit.getWorld(getLevelName());
        if (world != null) {
            WeatherChangeEvent weather = new WeatherChangeEvent(world, bl);
            Bukkit.getServer().getPluginManager().callEvent(weather);
            if (weather.isCancelled()) {
                ci.cancel();
            }
        }
        // CraftBukkit end
    }
    
    @Inject(method = "setDifficulty", at = @At(value = "RETURN"))
    private void cerium$setDifficulty(Difficulty difficulty, CallbackInfo ci) {
        // CraftBukkit start
        ClientboundChangeDifficultyPacket packet = new ClientboundChangeDifficultyPacket(this.getDifficulty(), this.isDifficultyLocked());
        for (ServerPlayer player : (java.util.List<ServerPlayer>) (java.util.List) world.players()) {
            player.connection.send(packet);
        }
        // CraftBukkit end
    }

    // CraftBukkit start - Check if the name stored in NBT is the correct one
    @Override
    public void checkName(String name) {
        if (!this.settings.levelName.equals(name)) {
            this.settings.levelName = name;
        }
    }
    // CraftBukkit end
}
