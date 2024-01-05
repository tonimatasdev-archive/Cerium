package dev.tonimatas.cerium.mixins.network.protocol;

import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.util.thread.BlockableEventLoop;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PacketUtils.class)
public class PacketUtilsMixin {
    @Shadow @Final private static Logger LOGGER;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> packet, T packetListener, BlockableEventLoop<?> blockableEventLoop) throws RunningOnDifferentThreadException {
        if (!blockableEventLoop.isSameThread()) {
            blockableEventLoop.executeIfPossible(() -> {
                if (MinecraftServer.getServer().hasStopped() || (packetListener instanceof ServerCommonPacketListenerImpl && ((ServerCommonPacketListenerImpl) packetListener).processedDisconnect)) return; // CraftBukkit, MC-142590
                if (packetListener.shouldHandleMessage(packet)) {
                    try {
                        packet.handle(packetListener);
                    } catch (Exception var6) {
                        label25: {
                            if (var6 instanceof ReportedException) {
                                ReportedException reportedException = (ReportedException)var6;
                                if (reportedException.getCause() instanceof OutOfMemoryError) {
                                    break label25;
                                }
                            }

                            if (!packetListener.shouldPropagateHandlingExceptions()) {
                                LOGGER.error("Failed to handle packet {}, suppressing error", packet, var6);
                                return;
                            }
                        }

                        if (var6 instanceof ReportedException) {
                            ReportedException reportedException2 = (ReportedException)var6;
                            packetListener.fillCrashReport(reportedException2.getReport());
                            throw var6;
                        }

                        CrashReport crashReport = CrashReport.forThrowable(var6, "Main thread packet handler");
                        packetListener.fillCrashReport(crashReport);
                        throw new ReportedException(crashReport);
                    }
                } else {
                    LOGGER.debug("Ignoring packet due to disconnection: {}", packet);
                }

            });
            throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
            // CraftBukkit start - SPIGOT-5477, MC-142590
        } else if (MinecraftServer.getServer().hasStopped() || (packetListener instanceof ServerCommonPacketListenerImpl && ((ServerCommonPacketListenerImpl) packetListener).processedDisconnect)) {
            throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
            // CraftBukkit end
        }
    }
}
