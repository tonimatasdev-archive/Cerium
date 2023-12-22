package org.bukkit.craftbukkit.v1_20_R3.entity;

import net.minecraft.world.entity.projectile.EntityFireballFireball;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.SizedFireball;
import org.bukkit.inventory.ItemStack;

public class CraftSizedFireball extends CraftFireball implements SizedFireball {

    public CraftSizedFireball(CraftServer server, EntityFireballFireball entity) {
        super(server, entity);
    }

    @Override
    public ItemStack getDisplayItem() {
        if (getHandle().getItemRaw().isEmpty()) {
            return new ItemStack(Material.FIRE_CHARGE);
        } else {
            return CraftItemStack.asBukkitCopy(getHandle().getItemRaw());
        }
    }

    @Override
    public void setDisplayItem(ItemStack item) {
        getHandle().setItem(CraftItemStack.asNMSCopy(item));
    }

    @Override
    public EntityFireballFireball getHandle() {
        return (EntityFireballFireball) entity;
    }
}
