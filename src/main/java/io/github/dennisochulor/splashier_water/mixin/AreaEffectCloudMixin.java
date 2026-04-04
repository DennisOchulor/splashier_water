package io.github.dennisochulor.splashier_water.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.dennisochulor.splashier_water.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudMixin {

    @Shadow
    public abstract Entity getOwner();

    @Shadow
    private PotionContents potionContents;

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private void serverTick$entity(ServerLevel serverLevel, CallbackInfo ci, @Local(name = "entity") LivingEntity entity) {
        if (!potionContents.is(Potions.WATER)) return;

        Util.affectEntityWithWater(serverLevel, entity, (AreaEffectCloud) (Object) this, getOwner());
    }

    @ModifyExpressionValue(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionContents;hasEffects()Z"))
    private boolean serverTick$hasEffects(boolean original) {
        return potionContents.is(Potions.WATER) || original;
    }

    @ModifyExpressionValue(method = "serverTick", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;noneMatch(Ljava/util/function/Predicate;)Z"))
    private boolean serverTick$noneMatch(boolean original) {
        return !potionContents.is(Potions.WATER) && original;
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Ljava/util/Set;removeIf(Ljava/util/function/Predicate;)Z"))
    private void serverTick$block(ServerLevel serverLevel, CallbackInfo ci) {
        if (!potionContents.is(Potions.WATER)) return;

        AreaEffectCloud cloud = (AreaEffectCloud) (Object) this;
        BlockPos.betweenClosedStream(cloud.getBoundingBox()).forEach(blockPos -> Util.dowseBlock(serverLevel, blockPos, cloud, getOwner()));
    }
}
