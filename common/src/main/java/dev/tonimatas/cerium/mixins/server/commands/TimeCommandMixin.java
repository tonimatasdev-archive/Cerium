package dev.tonimatas.cerium.mixins.server.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.event.world.TimeSkipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Iterator;

@SuppressWarnings("rawtypes")
@Mixin(TimeCommand.class)
public class TimeCommandMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static int setTime(CommandSourceStack commandSourceStack, int i) {
        Iterator var2 = com.google.common.collect.Iterators.singletonIterator(commandSourceStack.getLevel()); // CraftBukkit - SPIGOT-6496: Only set the time for the world the command originates in

        while(var2.hasNext()) {
            ServerLevel serverLevel = (ServerLevel)var2.next();
            // CraftBukkit start
            TimeSkipEvent event = new TimeSkipEvent(serverLevel.getWorld(), TimeSkipEvent.SkipReason.COMMAND, i - serverLevel.getDayTime());
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                serverLevel.setDayTime((long) serverLevel.getDayTime() + event.getSkipAmount());
            }
            // CraftBukkit end
        }

        commandSourceStack.sendSuccess(() -> {
            return Component.translatable("commands.time.set", new Object[]{i});
        }, true);
        return TimeCommand.getDayTime(commandSourceStack.getLevel());
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static int addTime(CommandSourceStack commandSourceStack, int i) {
        Iterator var2 = com.google.common.collect.Iterators.singletonIterator(commandSourceStack.getLevel()); // CraftBukkit - SPIGOT-6496: Only set the time for the world the command originates in

        while(var2.hasNext()) {
            ServerLevel serverLevel = (ServerLevel)var2.next();
            // CraftBukkit start
            TimeSkipEvent event = new TimeSkipEvent(serverLevel.getWorld(), TimeSkipEvent.SkipReason.COMMAND, i);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                serverLevel.setDayTime(serverLevel.getDayTime() + event.getSkipAmount());
            }
            // CraftBukkit end
        }

        int j = TimeCommand.getDayTime(commandSourceStack.getLevel());
        commandSourceStack.sendSuccess(() -> {
            return Component.translatable("commands.time.set", new Object[]{j});
        }, true);
        return j;
    }
}
