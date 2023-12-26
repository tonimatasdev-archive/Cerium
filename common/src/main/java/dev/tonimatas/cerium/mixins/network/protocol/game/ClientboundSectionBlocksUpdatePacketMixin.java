package dev.tonimatas.cerium.mixins.network.protocol.game;

import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;

@Mixin(ClientboundSectionBlocksUpdatePacket.class)
public class ClientboundSectionBlocksUpdatePacketMixin {
    @Shadow @Final @Mutable private SectionPos sectionPos;
    @Shadow @Final @Mutable private short[] positions;
    @Shadow @Final @Mutable private BlockState[] states;

    @Unique
    public void cerium$constructor(SectionPos sectionposition, ShortSet shortset, BlockState[] states) {
        this.sectionPos = sectionposition;
        this.positions = shortset.toShortArray();
        this.states = states;
    }
}
