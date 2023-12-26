package dev.tonimatas.cerium.bridge.network.chat;

import net.minecraft.network.chat.Component;

import java.util.Iterator;
import java.util.stream.Stream;

public interface ComponentBridge {
    Stream<Component> bridge$stream();

    Iterator<Component> bridge$iterator();
}
