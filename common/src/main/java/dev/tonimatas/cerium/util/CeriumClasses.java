package dev.tonimatas.cerium.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.bukkit.World;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

public class CeriumClasses {
    // CraftBukkit start
    public record UnknownPayload(ResourceLocation id, io.netty.buffer.ByteBuf data) implements CustomPacketPayload {
        @Override
        public @NotNull ResourceLocation id() {
            return id;
        }

        @Override
        public void write(FriendlyByteBuf packetdataserializer) {
            packetdataserializer.writeBytes(data);
        }

        @Override
        public ByteBuf data() {
            return data;
        }
    }
    // CraftBukkit end

    // Cerium start
    public record WorldInfo(ChunkGenerator gen, BiomeProvider biomeProvider, World.Environment env) {
        
    }
    // Cerium end

    // CraftBukkit start
    public static class ProcessableEffect {

        public MobEffect type;
        public MobEffectInstance effect;
        public final EntityPotionEffectEvent.Cause cause;

        public ProcessableEffect(MobEffectInstance effect, EntityPotionEffectEvent.Cause cause) {
            this.effect = effect;
            this.cause = cause;
        }

        public ProcessableEffect(MobEffect type, EntityPotionEffectEvent.Cause cause) {
            this.type = type;
            this.cause = cause;
        }
    }
    // CraftBukkit end
}
