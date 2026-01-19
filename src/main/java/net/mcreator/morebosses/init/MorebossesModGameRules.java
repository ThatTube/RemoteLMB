
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.level.GameRules;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MorebossesModGameRules {
	public static final GameRules.Key<GameRules.BooleanValue> PERMA_TURRETS = GameRules.register("permaTurrets", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
}
