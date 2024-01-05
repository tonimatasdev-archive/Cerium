package dev.tonimatas.cerium.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
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
}
