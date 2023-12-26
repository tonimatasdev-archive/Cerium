package dev.tonimatas.cerium.mixins.network.chat;

import com.google.common.collect.Streams;
import dev.tonimatas.cerium.bridge.network.chat.ComponentBridge;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(Component.class)
public interface ComponentMixin extends ComponentBridge, Iterable<Component> {
    @Shadow List<Component> getSiblings();

    @Unique
    default Stream<Component> cerium$stream() {
        class Func implements Function<Component, Stream<? extends Component>> {

            @Override
            public Stream<? extends Component> apply(Component component) {
                return ((ComponentBridge) component).bridge$stream();
            }
        }
        return Streams.concat(Stream.of((Component) this), this.getSiblings().stream().flatMap(new Func()));
    }

    @Override
    default @NotNull Iterator<Component> iterator() {
        return this.cerium$stream().iterator();
    }

    @Override
    default Stream<Component> bridge$stream() {
        return cerium$stream();
    }

    @Override
    default Iterator<Component> bridge$iterator() {
        return iterator();
    }
}
