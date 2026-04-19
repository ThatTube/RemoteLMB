
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;

import net.mcreator.morebosses.client.renderer.YakRenderer;
import net.mcreator.morebosses.client.renderer.WitherMissileRenderer;
import net.mcreator.morebosses.client.renderer.WindBurstRenderer;
import net.mcreator.morebosses.client.renderer.WaterStriderRenderer;
import net.mcreator.morebosses.client.renderer.TurretRenderer;
import net.mcreator.morebosses.client.renderer.TallEngineerRenderer;
import net.mcreator.morebosses.client.renderer.StrikerRenderer;
import net.mcreator.morebosses.client.renderer.SoulsperRenderer;
import net.mcreator.morebosses.client.renderer.SoulDionaeaRenderer;
import net.mcreator.morebosses.client.renderer.SmagmarRenderer;
import net.mcreator.morebosses.client.renderer.SlagtioTheMightyRenderer;
import net.mcreator.morebosses.client.renderer.ShrimpRenderer;
import net.mcreator.morebosses.client.renderer.ShockWaveRenderer;
import net.mcreator.morebosses.client.renderer.RobotWhaleRenderer;
import net.mcreator.morebosses.client.renderer.PiglinBoxerRenderer;
import net.mcreator.morebosses.client.renderer.PiglinBoulusRenderer;
import net.mcreator.morebosses.client.renderer.OilEngineerRenderer;
import net.mcreator.morebosses.client.renderer.MunaeaBetaRenderer;
import net.mcreator.morebosses.client.renderer.MissileRenderer;
import net.mcreator.morebosses.client.renderer.MinilotlRenderer;
import net.mcreator.morebosses.client.renderer.MaxolotRenderer;
import net.mcreator.morebosses.client.renderer.MagmaPitRenderer;
import net.mcreator.morebosses.client.renderer.LesserAnteaterRenderer;
import net.mcreator.morebosses.client.renderer.LamentBombRenderer;
import net.mcreator.morebosses.client.renderer.GuardianoOfTheEyesRenderer;
import net.mcreator.morebosses.client.renderer.EyeSentinelRenderer;
import net.mcreator.morebosses.client.renderer.EngineerRenderer;
import net.mcreator.morebosses.client.renderer.EnderSnakeRenderer;
import net.mcreator.morebosses.client.renderer.EnderLaserBeamRenderer;
import net.mcreator.morebosses.client.renderer.EnderBombRenderer;
import net.mcreator.morebosses.client.renderer.DryBonesRenderer;
import net.mcreator.morebosses.client.renderer.CopperMonstrosityRenderer;
import net.mcreator.morebosses.client.renderer.CopperGrablerRenderer;
import net.mcreator.morebosses.client.renderer.BugsRenderer;
import net.mcreator.morebosses.client.renderer.BruteEngineerRenderer;
import net.mcreator.morebosses.client.renderer.BeggarWolfRenderer;
import net.mcreator.morebosses.client.renderer.ArchdukeLytherionRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MorebossesModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MorebossesModEntities.COPPER_MONSTROSITY.get(), CopperMonstrosityRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.SHOCK_WAVE.get(), ShockWaveRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.MAXOLOT.get(), MaxolotRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.DRY_BONES.get(), DryBonesRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.MINILOTL.get(), MinilotlRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.WIND_BURST.get(), WindBurstRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.SOUL_DIONAEA.get(), SoulDionaeaRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.BEGGAR_WOLF.get(), BeggarWolfRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.ENGINEER.get(), EngineerRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.BRUTE_ENGINEER.get(), BruteEngineerRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.TURRET.get(), TurretRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.TURRET_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.TALL_ENGINEER.get(), TallEngineerRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.OIL_DROP.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.OIL_ENGINEER.get(), OilEngineerRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.COPPER_EYE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.COPPER_GRABLER.get(), CopperGrablerRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.WITHER_MISSILE.get(), WitherMissileRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.MISSILE.get(), MissileRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.ROBOT_WHALE.get(), RobotWhaleRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.LASER.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.SHRIMP.get(), ShrimpRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.PIGLIN_BOULUS.get(), PiglinBoulusRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.PIGLIN_BOXER.get(), PiglinBoxerRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.ARCHDUKE_LYTHERION.get(), ArchdukeLytherionRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.LAMENT_BOMB.get(), LamentBombRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.MAGMA_PIT.get(), MagmaPitRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.SLAGTIO_THE_MIGHTY.get(), SlagtioTheMightyRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.EYE_OLD.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.GUARDIANO_OF_THE_EYES.get(), GuardianoOfTheEyesRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.ENDER_BOMB.get(), EnderBombRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.REVERSE_PEARL_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.STRIKER.get(), StrikerRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.ENDER_LASER.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.ENDER_LASER_BEAM.get(), EnderLaserBeamRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.EYE_SENTINEL.get(), EyeSentinelRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.SOULSPER.get(), SoulsperRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.ENDER_SNAKE.get(), EnderSnakeRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.WATER_STRIDER.get(), WaterStriderRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.MUNAEA_BETA.get(), MunaeaBetaRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.LESSER_ANTEATER.get(), LesserAnteaterRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.BUGS.get(), BugsRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.YAK.get(), YakRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.SMAGMAR.get(), SmagmarRenderer::new);
	}
}
