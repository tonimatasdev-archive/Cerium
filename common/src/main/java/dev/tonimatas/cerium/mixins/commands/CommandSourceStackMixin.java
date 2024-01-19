package dev.tonimatas.cerium.mixins.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.tree.CommandNode;
import dev.tonimatas.cerium.bridge.commands.CommandSourceBridge;
import dev.tonimatas.cerium.bridge.commands.CommandSourceStackBridge;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandSourceStack.class)
public abstract class CommandSourceStackMixin implements CommandSourceStackBridge {
    @Shadow public abstract ServerLevel getLevel();
    @Shadow @Final private int permissionLevel;

    @Shadow @Final public CommandSource source;

    @Unique public volatile CommandNode currentCommand;

    @Override
    public void cerium$setCurrentCommand(CommandNode value) {
        this.currentCommand = value;
    }

    @Inject(method = "hasPermission", at = @At(value = "HEAD"), cancellable = true)
    private void cerium$hasPermission(int i, CallbackInfoReturnable<Boolean> cir) {
        CommandNode currentCommand = this.currentCommand;
        if (currentCommand != null) {
            cir.setReturnValue(bridge$hasPermission(i, org.bukkit.craftbukkit.v1_20_R3.command.VanillaCommandWrapper.getPermission(currentCommand)));
            cir.cancel();
        }
    }

    @Override
    public boolean bridge$hasPermission(int i, String bukkitPermission) {
        // World is null when loading functions
        return ((this.getLevel() == null || !getLevel().getCraftServer().ignoreVanillaPermissions) && this.permissionLevel >= i) || ((CommandSourceStackBridge) this).bridge$getBukkitSender().hasPermission(bukkitPermission);
    }

    @Redirect(method = "broadcastToAdmins", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;isOp(Lcom/mojang/authlib/GameProfile;)Z"))
    public boolean cerium$broadcastToAdmins(PlayerList instance, GameProfile gameProfile) {
        return instance.getPlayer(gameProfile.getId()).getBukkitEntity().hasPermission("minecraft.admin.command_feedback");
    }

    @Override
    public CommandSender bridge$getBukkitSender() {
        return ((CommandSourceBridge) source).bridge$getBukkitSender((CommandSourceStack) (Object) this);
    }
}
