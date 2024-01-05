package dev.tonimatas.cerium.mixins.server;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.util.TimeUtil;
import org.spongepowered.asm.mixin.*;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(ServerTickRateManager.class)
public abstract class ServerTickRateManagerMixin {
    @Shadow
    public abstract boolean stopSprinting();
    @Shadow private long scheduledCurrentSprintTicks;
    @Shadow private long sprintTimeSpend;
    @Shadow private long remainingSprintTicks;
    @Shadow private boolean previousIsFrozen;

    @Shadow public abstract void setFrozen(boolean bl);

    @Shadow @Final private MinecraftServer server;
    @Unique
    private AtomicBoolean cerium$sendLog = new AtomicBoolean(true);

    @Unique
    public boolean stopSprinting(boolean sendLog) {
        cerium$sendLog.set(sendLog);
        return stopSprinting();
    }

    @Unique
    private void finishTickSprint(boolean sendLog) {
        cerium$sendLog .set(sendLog);
        finishTickSprint();
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private void finishTickSprint() {
        long l = this.scheduledCurrentSprintTicks - this.remainingSprintTicks;
        double d = Math.max(1.0, (double)this.sprintTimeSpend) / (double) TimeUtil.NANOSECONDS_PER_MILLISECOND;
        int i = (int)((double)(TimeUtil.MILLISECONDS_PER_SECOND * l) / d);
        String string = String.format("%.2f", l == 0L ? (double)((ServerTickRateManager) (Object) this).millisecondsPerTick() : d / (double)l);
        this.scheduledCurrentSprintTicks = 0L;
        this.sprintTimeSpend = 0L;
        // CraftBukkit start - add sendLog parameter
        if (cerium$sendLog.getAndSet(true)) {
            this.server.createCommandSourceStack().sendSuccess(() -> {
                return Component.translatable("commands.tick.sprint.report", i, string);
            }, true);
        }
        // CraftBukkit end
        this.remainingSprintTicks = 0L;
        this.setFrozen(this.previousIsFrozen);
        this.server.onTickRateChanged();
    }
}