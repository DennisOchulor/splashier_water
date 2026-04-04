package io.github.dennisochulor.splashier_water.mixin;

import io.github.dennisochulor.splashier_water.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    @Shadow
    private ItemStack pickupItemStack;

    public AbstractArrowMixin(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void onHitEntity$extinguish(EntityHitResult hitResult, CallbackInfo ci) {
        PotionContents potionContents = pickupItemStack.get(DataComponents.POTION_CONTENTS);
        if (potionContents == null || !potionContents.is(Potions.WATER)) return;

        if (hitResult.getEntity() instanceof LivingEntity livingEntity && level() instanceof ServerLevel serverLevel) {
            Util.affectEntityWithWater(serverLevel, livingEntity, this, getOwner());
        }
    }

    @Inject(method = "onHitBlock", at = @At("RETURN"))
    private void onHitBlock$dowse(BlockHitResult hitResult, CallbackInfo ci) {
        PotionContents potionContents = pickupItemStack.get(DataComponents.POTION_CONTENTS);
        if (potionContents == null || !potionContents.is(Potions.WATER)) return;

        if (level() instanceof ServerLevel serverLevel) {
            Util.dowseBlock(serverLevel, hitResult.getBlockPos(), this, getOwner());
            Util.dowseBlock(serverLevel, hitResult.getBlockPos().relative(hitResult.getDirection()), this, getOwner());
        }
    }
}
