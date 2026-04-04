package io.github.dennisochulor.splashier_water.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.core.Direction.*;

@Mixin(AbstractThrownPotion.class)
public abstract class AbstractThrownPotionMixin {

	@Shadow
	protected abstract void onHitAsPotion(ServerLevel level, ItemStack potionItem, HitResult hitResult);

	@Shadow
	protected abstract void dowseFire(BlockPos pos);

	@Inject(method = "onHit", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/AbstractThrownPotion;onHitAsWater(Lnet/minecraft/server/level/ServerLevel;)V"))
	private void init(HitResult hitResult, CallbackInfo ci, @Local(name = "level") ServerLevel level,
					  @Local(name = "potionItemStack") ItemStack potionItemStack) {
		onHitAsPotion(level, potionItemStack, hitResult);
	}

	@Inject(method = "onHitBlock", at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/AbstractThrownPotion;dowseFire(Lnet/minecraft/core/BlockPos;)V"), cancellable = true)
	private void dowseFire$3x3x2(BlockHitResult hitResult, CallbackInfo ci, @Local(name = "potion") PotionContents potion) {
		if (!potion.is(Potions.WATER)) return;

		BlockPos hitPos = hitResult.getBlockPos();
		Direction hitDirection = hitResult.getDirection();
		BlockPos corner1;
		BlockPos corner2;

		if (hitDirection == UP || hitDirection == DOWN) {
			// horizontal dowsing
			corner1 = hitPos.offset(1, 0, 1);
			corner2 = hitPos.offset(-1, hitDirection == UP ? 1 : -1, -1);
		}
		else {
			// vertical dowsing
			Direction.Axis twoLongAxis = hitDirection.getAxis();
			Direction.Axis threeLongAxis = twoLongAxis == Axis.X ? Axis.Z : Axis.X;
			corner1 = hitPos.above().relative(threeLongAxis, 1);
			corner2 = hitPos.below().relative(threeLongAxis, -1).relative(hitDirection);
		}

		BlockPos.betweenClosedStream(corner1, corner2).forEach(this::dowseFire);
		ci.cancel();
	}
}