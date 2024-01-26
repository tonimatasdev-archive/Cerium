package dev.tonimatas.cerium.mixins.world.entity.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.PrimedTnt;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.entity.Explosive;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin {
    @Shadow public abstract int getFuse();

    @Shadow public abstract void setFuse(int i);

    @Unique public float yield = 4; // CraftBukkit - add field
    @Unique public boolean isIncendiary = false; // CraftBukkit - add field
    
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void tick() {
        if (!((PrimedTnt) (Object) this).isNoGravity()) {
            ((PrimedTnt) (Object) this).setDeltaMovement(((PrimedTnt) (Object) this).getDeltaMovement().add(0.0, -0.04, 0.0));
        }

        ((PrimedTnt) (Object) this).move(MoverType.SELF, ((PrimedTnt) (Object) this).getDeltaMovement());
        ((PrimedTnt) (Object) this).setDeltaMovement(((PrimedTnt) (Object) this).getDeltaMovement().scale(0.98));
        if (((PrimedTnt) (Object) this).onGround()) {
            ((PrimedTnt) (Object) this).setDeltaMovement(((PrimedTnt) (Object) this).getDeltaMovement().multiply(0.7, -0.5, 0.7));
        }

        int i = this.getFuse() - 1;
        this.setFuse(i);
        if (i <= 0) {
            ((PrimedTnt) (Object) this).discard();
            if (!((PrimedTnt) (Object) this).level().isClientSide) {
                this.explode();
            }
        } else {
            ((PrimedTnt) (Object) this).updateInWaterStateAndDoFluidPushing();
            if (((PrimedTnt) (Object) this).level().isClientSide) {
                ((PrimedTnt) (Object) this).level().addParticle(ParticleTypes.SMOKE, ((PrimedTnt) (Object) this).getX(), ((PrimedTnt) (Object) this).getY() + 0.5, ((PrimedTnt) (Object) this).getZ(), 0.0, 0.0, 0.0);
            }
        }

    }
    
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private void explode() {
        // CraftBukkit start
        // float f = 4.0F;
        ExplosionPrimeEvent event = CraftEventFactory.callExplosionPrimeEvent((Explosive) this.getBukkitEntity());
        if (!event.isCancelled()) {
            ((PrimedTnt) (Object) this).level().explode(this, ((PrimedTnt) (Object) this).getX(), ((PrimedTnt) (Object) this).getY(0.0625D), ((PrimedTnt) (Object) this).getZ(), event.getRadius(), event.getFire(), World.a.TNT);
        }
        // CraftBukkit end
    }
}
