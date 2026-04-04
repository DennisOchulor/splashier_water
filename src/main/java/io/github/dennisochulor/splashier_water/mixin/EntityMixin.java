package io.github.dennisochulor.splashier_water.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyReturnValue(method = "fireImmune", at = @At("RETURN"))
    private boolean makeWaterArrowFireImmune(boolean original) {
        Entity thisEntity = (Entity) (Object) this;

        if (thisEntity instanceof Arrow arrow) {
            PotionContents potionContents = arrow.getPickupItemStackOrigin().get(DataComponents.POTION_CONTENTS);

            if (potionContents != null && potionContents.is(Potions.WATER)) {
                return true;
            }
        }

        return original;
    }
}
