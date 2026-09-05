package io.github.dennisochulor.splashier_water;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.PotionTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import static net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion.WATER_SENSITIVE_OR_ON_FIRE;

/**
 * Stolen from {@link net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion}
 */
public final class Util {
    private Util() {}

    public static void affectEntityWithWater(ServerLevel serverLevel, LivingEntity affectedEntity, Entity sourceEntity, @Nullable Entity ownerEntity, PotionContents potion) {
        if (!WATER_SENSITIVE_OR_ON_FIRE.test(affectedEntity)) return;

        boolean hurtsWaterSensitiveEntities = potion.is(PotionTags.HURTS_WATER_SENSITIVE_ENTITIES);
        boolean extinguishesEntities = potion.is(PotionTags.EXTINGUISHES_ENTITIES);

        if (hurtsWaterSensitiveEntities && affectedEntity.isSensitiveToWater()) {
            affectedEntity.hurtServer(serverLevel, affectedEntity.damageSources().indirectMagic(sourceEntity, ownerEntity), 1.0F);
        }

        if (extinguishesEntities && affectedEntity.isOnFire() && affectedEntity.isAlive()) {
            affectedEntity.extinguishFire();
        }

        if (affectedEntity instanceof Axolotl axolotl && potion.is(PotionTags.REHYDRATES_AXOLOTLS)) {
            axolotl.rehydrate();
        }
    }

    public static void douseBlock(ServerLevel serverLevel, BlockPos pos, Entity sourceEntity, @Nullable Entity ownerEntity) {
        BlockState blockState = serverLevel.getBlockState(pos);

        if (blockState.is(BlockTags.FIRE)) {
            serverLevel.destroyBlock(pos, false, sourceEntity);
        } else if (AbstractCandleBlock.isLit(blockState)) {
            AbstractCandleBlock.extinguish(null, blockState, serverLevel, pos);
        } else if (CampfireBlock.isLitCampfire(blockState)) {
            serverLevel.levelEvent(null, 1009, pos, 0);
            CampfireBlock.douse(ownerEntity, serverLevel, pos, blockState);
            serverLevel.setBlockAndUpdate(pos, blockState.setValue(CampfireBlock.LIT, false));
        }
    }
}
