package io.github.dennisochulor.splashier_water;

import net.fabricmc.api.ModInitializer;

import net.minecraft.tags.PotionTags;
import net.minecraft.world.item.alchemy.PotionContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class SplashierWater implements ModInitializer {
	public static final String MOD_ID = "splashier_water";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Predicate<PotionContents> AFFECTS_ENTITY =
			potionContents -> potionContents.is(PotionTags.EXTINGUISHES_ENTITIES)
					|| potionContents.is(PotionTags.HURTS_WATER_SENSITIVE_ENTITIES);

	@Override
	public void onInitialize() {

	}
}