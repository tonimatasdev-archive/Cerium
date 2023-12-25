package dev.tonimatas.cerium.mixins;

import net.minecraft.CrashReport;
import net.minecraft.SystemReport;
import org.bukkit.craftbukkit.v1_20_R3.CraftCrashReport;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrashReport.class)
public class CrashReportMixin {
    @Shadow @Final private SystemReport systemReport;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void cerium$init(String string, Throwable throwable, CallbackInfo ci) {
        this.systemReport.setDetail("CraftBukkit Information", new CraftCrashReport());
    }
}
