package org.bukkit.craftbukkit.v1_20_R3.tag;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntityType;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.stream.Collectors;

public class CraftEntityTag extends CraftTag<net.minecraft.world.entity.EntityType<?>, EntityType> {

    public CraftEntityTag(Registry<net.minecraft.world.entity.EntityType<?>> registry, TagKey<net.minecraft.world.entity.EntityType<?>> tag) {
        super(registry, tag);
    }

    @Override
    public boolean isTagged(EntityType entity) {
        return CraftEntityType.bukkitToMinecraft(entity).is(tag);
    }

    @Override
    public Set<EntityType> getValues() {
        return getHandle().stream().map(Holder::value).map(CraftEntityType::minecraftToBukkit).collect(Collectors.toUnmodifiableSet());
    }
}
