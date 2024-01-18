package dev.tonimatas.cerium.mixins.server.rcon.thread;

import dev.tonimatas.cerium.bridge.server.rcon.RconConsoleSourceBridge;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.server.rcon.thread.RconClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.net.Socket;

@Mixin(RconClient.class)
public class RconThreadMixin {
    @Unique private RconConsoleSource rconConsoleSource;
    
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void cerium$init(ServerInterface serverInterface, String string, Socket socket, CallbackInfo ci) {
        this.rconConsoleSource = new RconConsoleSource((DedicatedServer) serverInterface);
        ((RconConsoleSourceBridge) this.rconConsoleSource).cerium$setSocketAddress(socket.getRemoteSocketAddress());
    }
    
    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/rcon/thread/RconClient;sendCmdResponse(ILjava/lang/String;)V", ordinal = 0))
    private void cerium$run(RconClient instance, int i, String string) throws IOException {
        instance.sendCmdResponse(i, instance.serverInterface.runCommand(this.rconConsoleSource, string));
    }
    
}
