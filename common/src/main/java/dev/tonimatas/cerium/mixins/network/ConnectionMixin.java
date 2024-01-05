package dev.tonimatas.cerium.mixins.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @Shadow private Channel channel;
    @Shadow @Nullable private Component disconnectedReason;
    @Shadow public abstract boolean isConnected();
    @Shadow @Nullable private volatile Component delayedDisconnect;
    @Unique public String hostname = ""; // CraftBukkit - add field

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void disconnect(Component component) {
        if (this.channel == null) {
            this.delayedDisconnect = component;
        }

        if (this.isConnected()) {
            this.channel.close();
            this.disconnectedReason = component;
        }

    }
}
